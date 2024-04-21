package com.dbs.assessment.security;

import com.dbs.assessment.enums.LoginStatus;
import com.dbs.assessment.exception.AccountLockedException;
import com.dbs.assessment.exception.UserNotFoundException;
import com.dbs.assessment.mapper.LoginTrackerMapper;
import com.dbs.assessment.model.LoginTracker;
import com.dbs.assessment.model.User;
import com.dbs.assessment.request.LoginRequest;
import com.dbs.assessment.service.JWTTokenService;
import com.dbs.assessment.service.LoginTrackerService;
import com.dbs.assessment.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public class LoginFilter extends AbstractAuthenticationProcessingFilter {

    private final RequestMatcher loginRequestMatcher = new AntPathRequestMatcher("/login", HttpMethod.POST.toString());
    @Autowired
    JWTTokenService jwtService;
    @Autowired
    BCryptPasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    private UserService userService;
    @Autowired
    private LoginTrackerService loginTrackerService;
    @Autowired
    private LoginTrackerMapper loginTrackerMapper;

    public LoginFilter(String string) {
        super(string);
    }

    public Authentication doFilter(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {

        Authentication auth = null;
        if (isLoginRequest(request)) {
            try {
                LoginRequest loginRequest = new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);
                log.info(String.format("Attempting Authentication for username : %s", loginRequest.getUserName()));
                List<LoginTracker> loginRecords = loginTrackerService.findByCreatedDateIsGreaterThanEqualAndCreatedDateIsLessThanEqualAndUserNameAndStatus(loginRequest.getUserName(), LoginStatus.FAILURE);
                if (loginRecords.size() > 3) {
                    throw new AccountLockedException("Account Locked, Try again after some time");
                }
                User user = userService.loadUserByUsername(loginRequest.getUserName());
                LoginTracker loginTracker = loginTrackerMapper.map(loginRequest);
                if (authenticate(user, loginRequest)) {
                    loginTracker.setStatus(LoginStatus.SUCCESS);
                    loginTrackerService.save(loginTracker);
                    auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                } else {
                    loginTracker.setStatus(LoginStatus.FAILURE);
                    loginTrackerService.save(loginTracker);
                    throw new UserNotFoundException("Authentication Failure, Invalid Password");
                }
            } catch (Exception e) {
                response.setContentType("application/json");
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, List<String>> errorMessagesMap = new HashMap<>();
                errorMessagesMap.put("errorMessages", List.of(e.getMessage()));
                response.getWriter().write(objectMapper.writeValueAsString(errorMessagesMap));
                log.error(e.getMessage(), e);
            }
        }
        return auth;
    }

    private Boolean authenticate(UserDetails userDetails, LoginRequest loginRequest) {
        return passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword());
    }

    private Boolean authenticate(User user, LoginRequest loginRequest) {
        return passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
    }

    @Override
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        return super.requiresAuthentication(request, response) && isLoginRequest(request);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        return doFilter(request, response);
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult)
            throws IOException, ServletException {
        if (authResult != null) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json;charset=UTF-8");
            response.setHeader("Cache-Control", "no-cache");
            User user = (User) authResult.getPrincipal();
            APIToken apiToken = new APIToken(user.getId(), user.getUsername());
            String token = jwtService.generateToken(apiToken);
            response.setContentType("application/json");
            Cookie cookie = new Cookie(JWTTokenService.JWT_COOKIE_NAME, token);
            cookie.setPath("/");
            cookie.setMaxAge(24 * 3600);
            cookie.isHttpOnly();
            response.addCookie(cookie);

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authResult);
            SecurityContextHolder.setContext(context);
        }
    }

    private boolean isLoginRequest(HttpServletRequest request) {
        return loginRequestMatcher.matches(request);
    }
}

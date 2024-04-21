package com.dbs.assessment.security;

import com.dbs.assessment.constant.URLConstants;
import com.dbs.assessment.exception.AccountLockedException;
import com.dbs.assessment.model.User;
import com.dbs.assessment.service.JWTTokenService;
import com.dbs.assessment.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final RequestMatcher loginRequestMatcher =
            new AntPathRequestMatcher(URLConstants.LOGIN_URL, HttpMethod.POST.toString());

    private final RequestMatcher registerRequestMatcher =
            new AntPathRequestMatcher(URLConstants.REGISTER_URL, HttpMethod.POST.toString());

    private final List<RequestMatcher> swaggerRequestMatcher = List.of(
            new AntPathRequestMatcher(URLConstants.SWAGGER_URL, HttpMethod.GET.toString()),
            new AntPathRequestMatcher(URLConstants.SWAGGER_API_DOCS_URL, HttpMethod.GET.toString()));

    private final RequestMatcher productPropularRequestMatcher =
            new AntPathRequestMatcher(URLConstants.PRODUCT_POPULAR_URL, HttpMethod.GET.toString());

    @Autowired
    UserService userService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private JWTTokenService jwtTokenService;

    public JwtAuthenticationFilter(String string) {
        super(string);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {

        Authentication auth = null;
        String jwtCookie = getJWTCookieValue(request);
        if (jwtCookie != null) {
            APIToken apiToken = jwtTokenService.parseToken(jwtCookie);
            User user = userService.loadUserByUsername(apiToken.getUserName());
            auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        }
        if (auth == null) {
//            if(response.getStatus() == 423){
//                throw  new AccountLockedException("Account Locked, Try again after some time");
//            }
            throw  new AccountLockedException("Account Locked, Try again after some time");

//            throw new BadCredentialsException("AUTH TOKEN MISSING");
        }
        return auth;
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult)
            throws IOException, ServletException {

        if (authResult != null) {
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authResult);
            SecurityContextHolder.setContext(context);
        }
        chain.doFilter(request, response);
    }


//    @Override
//    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
//        if(failed instanceof AccountLockedException)
//            throw new AccountLockedException("Authentication failed: " + failed.getMessage());
//        throw failed;
//    }

    private String getJWTCookieValue(HttpServletRequest request) {
        String cookieValue = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(JWTTokenService.JWT_COOKIE_NAME)) {
                    cookieValue = cookie.getValue();
                }
            }
        }
        return cookieValue;
    }

    @Override
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        return super.requiresAuthentication(request, response)
                && !isLoginRequest(request)
                && !isRegistrationRequest(request)
                && !isSwaggerRequest(request)
                && !isProductPopularRequest(request);
    }

    private boolean isLoginRequest(HttpServletRequest request) {
        return loginRequestMatcher.matches(request);
    }

    private boolean isRegistrationRequest(HttpServletRequest request) {
        return registerRequestMatcher.matches(request);
    }

    private boolean isSwaggerRequest(HttpServletRequest request) {
        return swaggerRequestMatcher.stream().anyMatch(antMatcher -> antMatcher.matches(request));
    }

    private boolean isProductPopularRequest(HttpServletRequest request) {
        return productPropularRequestMatcher.matches(request);
    }
}

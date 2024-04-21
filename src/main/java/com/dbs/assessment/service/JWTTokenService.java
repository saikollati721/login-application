package com.dbs.assessment.service;

import com.dbs.assessment.security.APIToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ClaimsBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.io.Serializable;
import java.util.Date;

@Service
@Log4j2
public class JWTTokenService implements Serializable {

    public static long TOKEN_VALIDITY;
    public static String JWT_SECRET;
    public static String AUTHORITIES_KEY;
    public static String JWT_COOKIE_NAME;

    @Value("${jwt.signing.key}")
    public void setJwtSecret(String privateName) {
        JWT_SECRET = privateName;
    }

    @Value("${jwt.token.validity}")
    public void setTokenValidity(Long validity) {
        TOKEN_VALIDITY = validity;
    }

    @Value("${jwt.authorities.key}")
    public void setAuthoritiesKey(String key) {
        AUTHORITIES_KEY = key;
    }

    @Value("${jwt.cookie.name}")
    public void setJwtCookieName(String cookieName) {
        JWT_COOKIE_NAME = cookieName;
    }

    //    public String getUsernameFromToken(String token) {
    //        return getClaimFromToken(token, Claims::getSubject);
    //    }
    //
    //    private Date getExpirationDateFromToken(String token) {
    //        return getClaimFromToken(token, Claims::getExpiration);
    //    }

    //    private  <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
    //        final Claims claims = getAllClaimsFromToken(token);
    //        return claimsResolver.apply(claims);
    //    }

    //    private Claims getAllClaimsFromToken(String token) {
    //        return Jwts.parser()
    //                .setSigningKey(JWT_SECRET)
    //                .parseClaimsJws(token)
    //                .getBody();
    //    }

    //    private Boolean isTokenExpired(String token) {
    //        final Date expiration = getExpirationDateFromToken(token);
    //        return expiration.before(new Date());
    //    }

    //    public String generateToken(Authentication authentication) {
    //        String authorities = authentication.getAuthorities().stream()
    //                .map(GrantedAuthority::getAuthority)
    //                .collect(Collectors.joining(","));
    //
    //        return Jwts.builder()
    //                .setSubject(authentication.getName())
    //                .claim(AUTHORITIES_KEY, authorities)
    //                .setIssuedAt(new Date(System.currentTimeMillis()))
    //                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY*1000))
    //                .signWith(SignatureAlgorithm.HS256, JWT_SECRET)
    //                .compact();
    //    }

    //    public Boolean validateToken(String token, UserDetails userDetails) {
    //        final String username = getUsernameFromToken(token);
    //        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    //    }
    //
    //    public UsernamePasswordAuthenticationToken getAuthenticationToken(final String token, final Authentication
    // existingAuth, final UserDetails userDetails) {
    //
    //        final JwtParser jwtParser = Jwts.parser().setSigningKey(JWT_SECRET);
    //
    //        final Jws<Claims> claimsJws = jwtParser.parseClaimsJws(token);
    //
    //        final Claims claims = claimsJws.getBody();
    //
    ////        final Collection<? extends GrantedAuthority> authorities =
    ////                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
    ////                        .map(SimpleGrantedAuthority::new)
    ////                        .collect(Collectors.toList());
    //
    //        return new UsernamePasswordAuthenticationToken(userDetails, "", new ArrayList<>());
    //    }

    public String generateToken(APIToken apiToken) {
        ClaimsBuilder claimsBuilder = Jwts.claims();
        claimsBuilder
                .subject(apiToken.getUserName())
                //                .add("role", apiToken.getRole())
                .add("userId", apiToken.getUserId())
                .add("username", apiToken.getUserName());
        Claims claims = claimsBuilder.build();
        return Jwts.builder()
                .claims(claims)
                .signWith(getSecretKey())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY * 1000))
                .compact();
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(JWT_SECRET));
    }

    public APIToken parseToken(String token) {
        try {
            Claims body = (Claims) Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parse(token)
                    .getPayload();

//            UserRole userRole = null;
            Long userId = null;
            //            if (body.get("role") != null) {
            //                userRole = Enum.valueOf(UserRole.class, (String) body.get("role"));
            //            }
            if (body.get("userId") != null) {
                userId = Long.parseLong(body.get("userId").toString());
            }
            return new APIToken(userId, body.getSubject());
        } catch (JwtException | ClassCastException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}

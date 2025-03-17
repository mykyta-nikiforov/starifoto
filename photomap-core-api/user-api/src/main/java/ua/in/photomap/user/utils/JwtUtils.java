package ua.in.photomap.user.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.in.photomap.user.model.Privilege;
import ua.in.photomap.user.model.Role;
import ua.in.photomap.user.model.User;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility method for dealing with JWT.
 */
@Service
public class JwtUtils {

    public static final String APP_NAME = "photomap";
    public static final String SCOPE_ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final String SCOPE_REFRESH_TOKEN = "REFRESH_TOKEN";

    @Value("${photomap.security.secret}")
    private String secret;
    @Value("${photomap.security.jwt.expirationDateInMs}")
    private int jwtExpirationInMs;
    @Value("${photomap.security.jwt.refreshExpirationDateInMs}")
    private int refreshExpirationDateInMs;

    public String generateJwt(User user) {
        Algorithm algorithm = Algorithm.HMAC512(secret);
        return JWT.create()
                .withIssuer(APP_NAME)
                .withSubject(user.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .withClaim("scope", SCOPE_ACCESS_TOKEN)
                .withClaim("privileges", mapPrivileges(user.getRoles()))
                .withClaim("user_id", user.getId())
                .sign(algorithm);
    }

    private List<String> mapPrivileges(Set<Role> roles) {
        return roles.stream()
                .flatMap(role -> role.getPrivileges().stream())
                .map(Privilege::getName)
                .distinct()
                .collect(Collectors.toList());
    }

    public String generateRefreshToken(String email) {
        Algorithm algorithm = Algorithm.HMAC512(secret);
        long expirationInMs = refreshExpirationDateInMs;
        return JWT.create()
                .withIssuer(APP_NAME)
                .withClaim("scope", SCOPE_REFRESH_TOKEN)
                .withSubject(email)
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationInMs))
                .sign(algorithm);
    }

    public boolean shouldUpdateRefreshToken(String currentRefreshToken) {
        Algorithm algorithm = Algorithm.HMAC512(secret);
        DecodedJWT jwt = JWT.require(algorithm).withIssuer(APP_NAME).build().verify(currentRefreshToken);
        return jwt.getExpiresAt().before(new Date(System.currentTimeMillis() + jwtExpirationInMs));
    }

    public long getTokenExpiresIn(String token) {
        Algorithm algorithm = Algorithm.HMAC512(secret);
        DecodedJWT jwt = JWT.require(algorithm).withIssuer(APP_NAME).build().verify(token);
        return jwt.getExpiresAt().getTime() - new Date().getTime();
    }
}

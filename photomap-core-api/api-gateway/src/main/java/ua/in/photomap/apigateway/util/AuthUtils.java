package ua.in.photomap.apigateway.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public final class AuthUtils {
    private static final String AUTH_HEADER = "Authorization";
    private static final String AUTH_HEADER_PREFIX = "Bearer ";
    private static final String ACCESS_TOKEN_COOKIE = "accessToken";

    @Value("${photomap.security.secret}")
    private String jwtSecret;

    public Optional<String> getAuthToken(ServerHttpRequest request) {
        return getAuthHeaderToken(request)
                .or(() -> CookieUtils.getCookieValue(request, ACCESS_TOKEN_COOKIE));
    }

    private Optional<String> getAuthHeaderToken(ServerHttpRequest request) {
        return request.getHeaders().getOrEmpty(AUTH_HEADER)
                .stream()
                .filter(header -> header.startsWith(AUTH_HEADER_PREFIX))
                .findFirst()
                .map(header -> header.substring(AUTH_HEADER_PREFIX.length()));
    }

    public DecodedJWT getDecodedJWT(String token) {
        try {
            return JWT.require(Algorithm.HMAC512(jwtSecret))
                    .build()
                    .verify(token);
        } catch (Exception e) {
            log.warn("Failed to decode JWT token: {}", e.getMessage());
            throw e;
        }
    }
}

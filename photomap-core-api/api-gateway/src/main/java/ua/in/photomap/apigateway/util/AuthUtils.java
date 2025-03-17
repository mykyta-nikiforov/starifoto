package ua.in.photomap.apigateway.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public final class AuthUtils {

    @Value("${photomap.security.secret}")
    private String jwtSecret;

    public static final String AUTH_HEADER_PREFIX = "Bearer ";

    public Optional<String> getAuthToken(ServerHttpRequest request) {
        Optional<String> result = getAuthHeaderToken(request);
        if (result.isEmpty()) {
            result = getCookieByName(request, "accessToken");
        }
        return result;
    }

    private Optional<String> getAuthHeaderToken(ServerHttpRequest request) {
        return request.getHeaders().getOrEmpty("Authorization")
                .stream()
                .filter(header -> header.startsWith(AUTH_HEADER_PREFIX))
                .findFirst()
                .map(header -> header.substring(AUTH_HEADER_PREFIX.length()));
    }

    private static Optional<String> getCookieByName(ServerHttpRequest request, String cookieName) {
        return Optional.ofNullable(request.getCookies().getFirst(cookieName))
                .filter(cookie -> !cookie.getValue().isEmpty())
                .map(HttpCookie::getValue);
    }

    public DecodedJWT getDecodedJWT(String token) {
        return JWT.require(Algorithm.HMAC512(jwtSecret))
                .build()
                .verify(token);
    }
}

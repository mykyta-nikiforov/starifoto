package ua.in.photomap.apigateway.util;

import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CookieUtils {
    public static void clearCookie(ServerHttpResponse response, String cookieName) {
        ResponseCookie cookie = ResponseCookie.from(cookieName, "")
                .path("/")
                .maxAge(0)
                .build();
        response.addCookie(cookie);
    }

    public static Optional<String> getCookieValue(ServerHttpRequest request, String cookieName) {
        return Optional.ofNullable(request.getCookies().getFirst(cookieName))
                .filter(cookie -> !cookie.getValue().isEmpty())
                .map(HttpCookie::getValue);
    }
} 
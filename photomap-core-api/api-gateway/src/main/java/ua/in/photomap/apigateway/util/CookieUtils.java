package ua.in.photomap.apigateway.util;

import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

@Component
public class CookieUtils {
    public static void clearCookie(ServerHttpResponse response, String cookieName) {
        ResponseCookie cookie = ResponseCookie.from(cookieName, "")
                .path("/")
                .maxAge(0)
                .build();
        response.addCookie(cookie);
    }
} 
package ua.in.photomap.notificationapi.interceptor.websocket;

import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.WebUtils;

import java.util.Map;

@Slf4j
public class CsrfHandshakeInterceptor implements HandshakeInterceptor {
    private static final String CSRF_COOKIE_NAME = "XSRF-TOKEN";
    public static final String CSRF_ATTR_NAME = "CSRF_TOKEN";

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                 WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            Cookie csrfCookie = WebUtils.getCookie(servletRequest.getServletRequest(), CSRF_COOKIE_NAME);
            if (csrfCookie != null) {
                attributes.put(CSRF_ATTR_NAME, csrfCookie.getValue());
                return true;
            }
            log.warn("No CSRF cookie found during handshake");
            return false;
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                             WebSocketHandler wsHandler, Exception exception) {
        // No action needed after handshake
    }
} 
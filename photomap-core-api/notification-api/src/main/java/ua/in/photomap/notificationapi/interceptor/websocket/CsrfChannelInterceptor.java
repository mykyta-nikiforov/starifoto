package ua.in.photomap.notificationapi.interceptor.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import ua.in.photomap.common.rest.toolkit.exception.ForbiddenException;

import java.util.Arrays;
import java.util.Map;

@Slf4j
@Component
public class CsrfChannelInterceptor implements ChannelInterceptor {
    private static final String CSRF_HEADER_NAME = "X-XSRF-TOKEN";
    private static final String CSRF_COOKIE_NAME = "XSRF-TOKEN";

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (accessor.getCommand() != null && accessor.getCommand().equals(StompCommand.CONNECT)) {
            Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
            if (sessionAttributes == null) {
                throw new ForbiddenException("No session attributes found");
            }

            // Get the CSRF token from header
            String headerToken = accessor.getFirstNativeHeader(CSRF_HEADER_NAME);
            if (headerToken == null) {
                throw new ForbiddenException("Missing CSRF token in header");
            }

            // Get the CSRF token from handshake attributes
            String cookieToken = (String) sessionAttributes.get(CsrfHandshakeInterceptor.CSRF_ATTR_NAME);
            if (cookieToken == null) {
                throw new ForbiddenException("No CSRF token found in session");
            }

            if (!headerToken.equals(cookieToken)) {
                log.warn("CSRF token mismatch - Header: {}, Cookie: {}", headerToken, cookieToken);
                throw new ForbiddenException("CSRF token validation failed");
            }

            log.debug("CSRF validation successful for WebSocket connection");
        }

        return message;
    }
} 
package ua.in.photomap.notificationapi.config;

import com.auth0.jwt.interfaces.Claim;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ua.in.photomap.common.rest.toolkit.exception.ForbiddenException;
import ua.in.photomap.common.rest.toolkit.util.JwtService;
import ua.in.photomap.common.rest.toolkit.util.SecurityUtils;

import java.util.Map;

@RequiredArgsConstructor
public class AuthChannelInterceptor implements ChannelInterceptor {
    private final JwtService jwtService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authToken = accessor.getFirstNativeHeader("Authorization");
            if (authToken != null && authToken.startsWith("Bearer ")) {
                String token = authToken.substring(7);
                try {
                    Map<String, Claim> claims = jwtService.getClaims(token);
                    jwtService.validateClaims(claims);
                    Authentication authentication = SecurityUtils.buildAuthentication(claims, token);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (Exception e) {
                    throw new ForbiddenException("Cannot establish connection. Invalid token.");
                }
            }
        }
        return message;
    }
}

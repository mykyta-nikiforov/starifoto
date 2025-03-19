package ua.in.photomap.notificationapi.interceptor.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import ua.in.photomap.common.rest.toolkit.exception.ForbiddenException;
import ua.in.photomap.common.rest.toolkit.model.JwtAuthenticatedUser;
import ua.in.photomap.common.rest.toolkit.model.TokenBasedAuthentication;

@Slf4j
public class TopicSubscriptionInterceptor implements ChannelInterceptor {
    private final String USER_TOPIC_DESTINATION = "/topic/user/";

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor= StompHeaderAccessor.wrap(message);
        if (isUserSubscriptionMessage(message, headerAccessor)) {
            if (!hasAccessToUserTopic(message)) {
                throw new ForbiddenException("Subscription not allowed");
            }
        }
        return message;
    }

    private boolean isUserSubscriptionMessage(Message<?> message, StompHeaderAccessor headerAccessor) {
        if (StompCommand.SUBSCRIBE.equals(headerAccessor.getCommand())) {
            String simpDestination = (String) message.getHeaders().get("simpDestination");
            return simpDestination != null && simpDestination.startsWith(USER_TOPIC_DESTINATION);
        }
        return false;
    }


    private boolean hasAccessToUserTopic(Message<?> message) {
        String simpDestination = (String) message.getHeaders().get("simpDestination");
        TokenBasedAuthentication simpUser = (TokenBasedAuthentication) message.getHeaders().get("simpUser");
        if (simpUser == null) {
            return false;
        }
        JwtAuthenticatedUser principal = (JwtAuthenticatedUser) simpUser.getPrincipal();
        if (principal == null) {
            return false;
        }
        Long userId = ((JwtAuthenticatedUser) simpUser.getPrincipal()).getId();
        Long topicUserId = Long.parseLong(simpDestination.substring(USER_TOPIC_DESTINATION.length()));
        return userId.equals(topicUserId);
    }
}


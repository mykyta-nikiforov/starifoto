package ua.in.photomap.notificationapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import lombok.extern.slf4j.Slf4j;
import ua.in.photomap.notificationapi.interceptor.websocket.TopicSubscriptionInterceptor;

@Configuration
@EnableWebSocketSecurity
@Slf4j
public class WebSocketSecurityConfig implements WebSocketMessageBrokerConfigurer {

    private final ChannelInterceptor csrfChannelInterceptor;

    public WebSocketSecurityConfig(ChannelInterceptor csrfChannelInterceptor) {
        this.csrfChannelInterceptor = csrfChannelInterceptor;
    }

    @Bean
    AuthorizationManager<Message<?>> messageAuthorizationManager(MessageMatcherDelegatingAuthorizationManager.Builder messages) {
        messages
                .nullDestMatcher().authenticated()
                .simpSubscribeDestMatchers("/topic/user/**").hasAnyRole("ADMIN", "USER", "MODERATOR")
                .anyMessage().denyAll();

        return messages.build();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(csrfChannelInterceptor, new TopicSubscriptionInterceptor());
    }
}

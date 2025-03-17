package ua.in.photomap.notificationapi.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.web.csrf.CsrfToken;

import java.util.Map;

@Configuration
@Slf4j
public class CsrfInterceptorConfig {

    private final static String CSRF_HEADER_NAME = "X-XSRF-TOKEN";

    @Bean("csrfChannelInterceptor")
    public ChannelInterceptor productionCsrfChannelInterceptor() {
        return new CsrfTokenChannelInterceptor();
    }

    private static class CsrfTokenChannelInterceptor implements ChannelInterceptor {
        @Override
        public Message<?> preSend(Message<?> message, MessageChannel channel) {
            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
            if (accessor.getCommand() != null && accessor.getCommand().equals(StompCommand.CONNECT)) {
                Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
                if (sessionAttributes != null) {
                    String csrfHeaderName = CSRF_HEADER_NAME;
                    String csrfTokenValue = accessor.getFirstNativeHeader(csrfHeaderName);
                    if (csrfTokenValue != null) {
                        sessionAttributes.put(CsrfToken.class.getName(), new CsrfToken() {
                            @Override
                            public String getHeaderName() {
                                return csrfHeaderName;
                            }

                            @Override
                            public String getParameterName() {
                                return "_csrf";
                            }

                            @Override
                            public String getToken() {
                                return csrfTokenValue;
                            }
                        });
                    }
                }
            }
            return message;
        }
    }
} 
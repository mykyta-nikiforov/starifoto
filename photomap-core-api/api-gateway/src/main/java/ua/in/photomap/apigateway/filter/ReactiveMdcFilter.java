package ua.in.photomap.apigateway.filter;

import com.google.common.base.Stopwatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ua.in.photomap.apigateway.util.AuthUtils;

import java.security.SecureRandom;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReactiveMdcFilter implements GlobalFilter, Ordered {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String ALLOWED_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int REQUEST_ID_LENGTH = 10;
    private static final String EMPTY_AUTH = "emptyAuth";

    private final AuthUtils authUtils;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return Mono.fromCallable(() -> {
            Map<String, String> contextMap = createMdcContextMap(exchange);
            Stopwatch stopwatch = Stopwatch.createStarted();
            return new RequestContext(contextMap, stopwatch);
        })
        .flatMap(context -> Mono.fromRunnable(() -> logRequestStart(context.contextMap))
            .then(chain.filter(exchange))
            .doOnSuccess(aVoid -> logRequestSuccess(context.contextMap, context.stopwatch))
            .doOnError(error -> logRequestError(context.contextMap, context.stopwatch, error)));
    }

    private Map<String, String> createMdcContextMap(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        
        MDC.put("requestId", generateRequestId());
        MDC.put("httpMethod", request.getMethod().name());
        MDC.put("uri", request.getPath().toString());
        MDC.put("userEmail", extractUserEmail(request));
        
        return MDC.getCopyOfContextMap();
    }

    private void logRequestStart(Map<String, String> contextMap) {
        MDC.setContextMap(contextMap);
        log.info("Request-processing-started");
        MDC.clear();
    }

    private void logRequestSuccess(Map<String, String> contextMap, Stopwatch stopwatch) {
        stopwatch.stop();
        MDC.setContextMap(contextMap);
        log.info("Request-processing-finished in: {}", stopwatch);
        MDC.clear();
    }

    private void logRequestError(Map<String, String> contextMap, Stopwatch stopwatch, Throwable error) {
        stopwatch.stop();
        MDC.setContextMap(contextMap);
        log.error("Request-processing-failed in: {}", stopwatch, error);
        MDC.clear();
    }

    private String extractUserEmail(ServerHttpRequest request) {
        return authUtils.getAuthToken(request)
                .map(this::extractEmailFromToken)
                .orElse(EMPTY_AUTH);
    }

    private String extractEmailFromToken(String token) {
        try {
            return authUtils.getDecodedJWT(token)
                    .getClaim("sub")
                    .asString();
        } catch (Exception e) {
            return EMPTY_AUTH;
        }
    }

    private String generateRequestId() {
        StringBuilder sb = new StringBuilder(REQUEST_ID_LENGTH);
        for (int i = 0; i < REQUEST_ID_LENGTH; i++) {
            sb.append(ALLOWED_CHARS.charAt(SECURE_RANDOM.nextInt(ALLOWED_CHARS.length())));
        }
        return sb.toString();
    }

    @Override
    public int getOrder() {
        return -1;
    }

    @lombok.Value
    private static class RequestContext {
        Map<String, String> contextMap;
        Stopwatch stopwatch;
    }
}

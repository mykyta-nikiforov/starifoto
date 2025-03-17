package ua.in.photomap.apigateway.filter;

import com.google.common.base.Stopwatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ua.in.photomap.apigateway.util.AuthUtils;

import java.util.Optional;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReactiveMdcFilter implements GlobalFilter, Ordered {
    private final AuthUtils authUtils;

    private static final Integer RANDOM_LEFT_LIMIT = 48; // numeral '0'
    private static final Integer RANDOM_RIGHT_LIMIT = 122; // letter 'z'
    private static final Integer RANDOM_TARGET_STRING_LENGTH = 10;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        MDC.put("requestId", generateRandomString());
        MDC.put("httpMethod", exchange.getRequest().getMethod().name());
        MDC.put("uri", exchange.getRequest().getPath().toString());
        Optional<String> authToken = authUtils.getAuthToken(exchange.getRequest());
        try {
            MDC.put("userEmail", authToken.isPresent()
                    ? authUtils.getDecodedJWT(authToken.get()).getClaim("sub").asString()
                    : "emptyAuth");
        } catch (Exception e) {
            MDC.put("userEmail", "emptyAuth");
        }

        final Stopwatch stopwatch = Stopwatch.createStarted();
        log.info("Request-processing-started");
        return chain.filter(exchange)
                .doOnSuccess(aVoid -> {
                    stopwatch.stop();
                    log.info("Request-processing-finished in: " + stopwatch);
                    MDC.clear();
                });
    }

    private String generateRandomString() {
        return new Random()
                .ints(RANDOM_LEFT_LIMIT, RANDOM_RIGHT_LIMIT + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))  // include only 0-9, A-Z, a-z
                .limit(RANDOM_TARGET_STRING_LENGTH)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}

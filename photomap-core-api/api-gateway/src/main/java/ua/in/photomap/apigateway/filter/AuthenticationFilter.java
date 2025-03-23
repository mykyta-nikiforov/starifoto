package ua.in.photomap.apigateway.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ua.in.photomap.apigateway.service.UserService;
import ua.in.photomap.apigateway.util.AuthUtils;
import ua.in.photomap.apigateway.util.CookieUtils;

import java.util.*;
import java.util.function.Supplier;

@RefreshScope
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationFilter implements GatewayFilter {
    private static final List<String> IGNORED_PATHS = Arrays.asList(
            "/api/auth/google",
            "/api/auth/login"
    );

    private final UserService userService;
    private final AuthUtils authUtils;
    private final CacheManager cacheManager;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        log.debug("Auth filter start for path: {}", request.getPath());

        if (isIgnoredPath(request)) {
            return chain.filter(exchange);
        }

        Optional<String> authToken = authUtils.getAuthToken(request);
        if (authToken.isEmpty()) {
            return chain.filter(exchange);
        }

        return validateToken(exchange, chain, authToken.get());
    }

    private boolean isIgnoredPath(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        return IGNORED_PATHS.stream()
                .anyMatch(path::contains);
    }

    private Mono<Void> validateToken(ServerWebExchange exchange, GatewayFilterChain chain, String token) {
        try {
            DecodedJWT jwt = authUtils.getDecodedJWT(token);
            Long userId = jwt.getClaim("user_id").asLong();
            Set<String> tokenPrivileges = new HashSet<>(jwt.getClaim("privileges").asList(String.class));

            log.debug("Validating auth for user: {}", userId);

            if (!validateUser(userId, tokenPrivileges)) {
                return onError(exchange, HttpStatus.FORBIDDEN);
            }

            return chain.filter(exchange);
        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return onError(exchange, HttpStatus.FORBIDDEN);
        }
    }

    private boolean validateUser(Long userId, Set<String> tokenPrivileges) {
        if (!getIsEnabled(userId)) {
            log.debug("User {} is not enabled", userId);
            return false;
        }

        if (!getIsNonLockedUser(userId)) {
            log.debug("User {} is locked", userId);
            return false;
        }

        Set<String> userPrivileges = getUserPrivileges(userId);
        boolean privilegesMatch = Objects.equals(tokenPrivileges, userPrivileges);
        log.debug("User {} privileges match: {}", userId, privilegesMatch);

        return privilegesMatch;
    }

    private Set<String> getUserPrivileges(Long userId) {
        return getCachedValue("getUserPrivileges", userId, () -> {
            Set<String> privileges = userService.getUserPrivileges(userId);
            return !privileges.isEmpty() ? privileges : null;
        });
    }

    private boolean getIsNonLockedUser(Long userId) {
        Boolean result = getCachedValue("isNonLockedUser", userId, () -> {
            boolean nonLocked = userService.isNonLockedUser(userId);
            return nonLocked ? true : null;
        });
        return result != null && result;
    }

    private boolean getIsEnabled(Long userId) {
        Boolean result = getCachedValue("isEnabledUser", userId, () -> {
            boolean enabled = userService.isEnabledUser(userId);
            return enabled ? true : null;
        });
        return result != null && result;
    }

    private <T> T getCachedValue(String cacheName, Long userId, Supplier<T> valueSupplier) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            log.warn("Cache {} not found", cacheName);
            return valueSupplier.get();
        }

        T cachedResult = cache.get(userId, (Class<T>) Object.class);
        if (cachedResult != null) {
            log.debug("Cache hit for {}: {}", cacheName, cachedResult);
            return cachedResult;
        }

        T result = valueSupplier.get();
        if (result != null) {
            cache.put(userId, result);
            log.debug("{} value cached for user {}", cacheName, userId);
        }

        return result;
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        CookieUtils.clearCookie(response, "accessToken");
        return response.setComplete();
    }
}

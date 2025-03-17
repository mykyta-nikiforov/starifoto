package ua.in.photomap.apigateway.filter;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ua.in.photomap.apigateway.service.UserService;
import ua.in.photomap.apigateway.util.AuthUtils;

import java.util.*;

@RefreshScope
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationFilter implements GatewayFilter {
    private final UserService userService;
    private final AuthUtils authUtils;
    private final CacheManager cacheManager;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.debug("Auth filter start.");
        ServerHttpRequest request = exchange.getRequest();
        Optional<String> authToken = authUtils.getAuthToken(request);
        try {
            if (authToken.isPresent() && requestIsNotLogin(request)) {
                String token = authToken.get();
                DecodedJWT jwt = authUtils.getDecodedJWT(token);
                Map<String, Claim> claims = jwt.getClaims();
                Long userId = claims.get("user_id").asLong();
                Set<String> userPrivileges = new HashSet<>(claims.get("privileges").asList(String.class));
                log.debug("Start auth check. User id: " + userId);
                boolean enabledUser = getIsEnabled(userId);
                if (!enabledUser) {
                    return this.onError(exchange, HttpStatus.I_AM_A_TEAPOT);
                }

                boolean nonLockedUser = getIsNonLockedUser(userId);
                log.debug("User non locked: " + nonLockedUser);
                Set<String> existUserPriviliges = getUserPrivileges(userId);
                log.debug("User privileges: " + userPrivileges);
                if (!nonLockedUser
                        || !Objects.equals(userPrivileges, existUserPriviliges)) {
                    return this.onError(exchange, HttpStatus.FORBIDDEN);
                }
            }
        } catch (Exception e) {
            log.warn("Auth filter caught exception: " + e.getMessage());
            return this.onError(exchange, HttpStatus.FORBIDDEN);
        }
        log.debug("Auth filter finish. Result: " + authToken.isPresent());
        return chain.filter(exchange);
    }

    private boolean requestIsNotLogin(ServerHttpRequest request) {
        return !request.getURI().getPath().contains("/api/auth/google")
                && !request.getURI().getPath().contains("/api/auth/login");
    }

    private Set<String> getUserPrivileges(Long userId) {
        Cache cache = cacheManager.getCache("getUserPrivileges");
        Set<String> cachedResult = cache.get(userId, Set.class);
        if (cachedResult != null) {
            log.debug("Cache hit for user privileges: " + cachedResult);
            return cachedResult;
        }
        Set<String> userPrivileges = userService.getUserPrivileges(userId);
        log.debug("User privileges: " + userPrivileges);
        if (userPrivileges != null && !userPrivileges.isEmpty()) {
            cache.put(userId, userPrivileges);
            log.debug("User privileges put to cache");
        }
        return userPrivileges;
    }

    private boolean getIsNonLockedUser(Long userId) {
        Cache cache = cacheManager.getCache("isNonLockedUser");
        Boolean cachedResult = cache.get(userId, Boolean.class);
        if (cachedResult != null) {
            log.debug("Cache hit for user non locked: " + cachedResult);
            return cachedResult;
        }
        boolean nonLockedUser = userService.isNonLockedUser(userId);
        log.debug("User non locked: " + nonLockedUser);
        if (nonLockedUser) {
            cache.put(userId, true);
            log.debug("User non locked put to cache");
        }
        return nonLockedUser;
    }

    private boolean getIsEnabled(Long userId) {
        Cache cache = cacheManager.getCache("isEnabledUser");
        Boolean cachedResult = cache.get(userId, Boolean.class);
        if (cachedResult != null) {
            log.debug("Cache hit for user enabled: " + cachedResult);
            return cachedResult;
        }
        boolean enabledUser = userService.isEnabledUser(userId);
        log.debug("User enabled: " + enabledUser);
        if (enabledUser) {
            cache.put(userId, true);
            log.debug("User enabled put to cache");
        }
        return enabledUser;
    }

    private void clearCookie(ServerHttpResponse response, String cookieName) {
        ResponseCookie cookie = ResponseCookie.from(cookieName, "accessToken") // Empty value
                .path("/")
                .maxAge(0)
                .build();
        response.addCookie(cookie);
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        clearCookie(response, "accessToken");

        return response.setComplete();
    }
}

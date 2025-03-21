package ua.in.photomap.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;

@Component
@Slf4j
@ConditionalOnExpression("${logging.filter.enabled:true}")
public class LoggingFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return Mono.justOrEmpty(exchange.<Route>getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR))
                .doOnNext(route -> log.debug("Incoming request is routed to id: {}", route.getId()))
                .then(chain.filter(exchange));
    }
}

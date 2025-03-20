package ua.in.photomap.apigateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ua.in.photomap.apigateway.filter.AuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class RouteConfig {
    private static final String URI_TEMPLATE = "http://%s:%d";

    private final AuthenticationFilter authFilter;

    @Value("${user-api.host}")
    private String userApiHost;

    @Value("${photo-api.host}")
    private String photoApiHost;

    @Value("${geojson-generator.host}")
    private String geojsonGeneratorHost;

    @Value("${notification-api.host}")
    private String notificationApiHost;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-api", r -> r
                        .path("/api/auth/**", "/api/user/**", "/doc/user-api/**")
                        .filters(f -> f.filter(authFilter))
                        .uri(getUri(userApiHost, 8082)))
                .route("photo-api", r -> r
                        .path("/api/photo/**", "/doc/photo-api/**")
                        .filters(f -> f.filter(authFilter))
                        .uri(getUri(photoApiHost, 8083)))
                .route("geojson-generator", r -> r
                        .path("/doc/geojson-generator/**")
                        .filters(f -> f.filter(authFilter))
                        .uri(getUri(geojsonGeneratorHost, 8088)))
                .route("notification-api", r -> r
                        .path("/doc/notification-api/**")
                        .filters(f -> f.filter(authFilter))
                        .uri(getUri(notificationApiHost, 8084)))
                .build();
    }

    private String getUri(String host, int port) {
        return URI_TEMPLATE.formatted(host, port);
    }
} 
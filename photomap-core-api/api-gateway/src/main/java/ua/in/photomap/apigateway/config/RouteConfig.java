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
    
    private static final String[] USER_API_PATHS = {
            "/api/auth/**",
            "/api/user/**",
            "/doc/user-api/**"
    };
    
    private static final String[] PHOTO_API_PATHS = {
            "/api/photo/**",
            "/doc/photo-api/**"
    };
    
    private static final String[] GEOJSON_GENERATOR_PATHS = {
            "/doc/geojson-generator/**"
    };
    
    private static final String[] NOTIFICATION_API_PATHS = {
            "/doc/notification-api/**"
    };

    private final AuthenticationFilter authFilter;

    @Value("${user-api.host}")
    private String userApiHost;
    @Value("${user-api.port}")
    private int userApiPort;

    @Value("${photo-api.host}")
    private String photoApiHost;
    @Value("${photo-api.port}")
    private int photoApiPort;

    @Value("${geojson-generator.host}")
    private String geojsonGeneratorHost;
    @Value("${geojson-generator.port}")
    private int geojsonGeneratorPort;

    @Value("${notification-api.host}")
    private String notificationApiHost;
    @Value("${notification-api.port}")
    private int notificationApiPort;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-api", r -> r
                        .path(USER_API_PATHS)
                        .filters(f -> f.filter(authFilter))
                        .uri(getUri(userApiHost, userApiPort)))
                .route("photo-api", r -> r
                        .path(PHOTO_API_PATHS)
                        .filters(f -> f.filter(authFilter))
                        .uri(getUri(photoApiHost, photoApiPort)))
                .route("geojson-generator", r -> r
                        .path(GEOJSON_GENERATOR_PATHS)
                        .filters(f -> f.filter(authFilter))
                        .uri(getUri(geojsonGeneratorHost, geojsonGeneratorPort)))
                .route("notification-api", r -> r
                        .path(NOTIFICATION_API_PATHS)
                        .filters(f -> f.filter(authFilter))
                        .uri(getUri(notificationApiHost, notificationApiPort)))
                .build();
    }

    private String getUri(String host, int port) {
        return URI_TEMPLATE.formatted(host, port);
    }
} 
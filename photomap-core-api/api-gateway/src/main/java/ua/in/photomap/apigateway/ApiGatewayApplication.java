package ua.in.photomap.apigateway;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import ua.in.photomap.apigateway.filter.AuthenticationFilter;
import ua.in.photomap.common.cache.config.CacheManagerConfigReference;

@SpringBootApplication
@RequiredArgsConstructor
@EnableCaching
@Import(CacheManagerConfigReference.class)
@Slf4j
public class ApiGatewayApplication {
    private final AuthenticationFilter filter;

    @Value("${user-api.host}")
    private String userApiHost;

    @Value("${photo-api.host}")
    private String photoApiHost;

    @Value("${geojson-generator.host}")
    private String geojsonGeneratorHost;

    @Value("${notification-api.host}")
    private String notificationApiHost;

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-api", r -> r.path("/api/auth/**", "/api/user/**", "/doc/user-api/**")
                        .filters(f -> f.filter(filter))
                        .uri("http://%s:8082".formatted(userApiHost)))
                .route("photo-api", r -> r.path("/api/photo/**", "/doc/photo-api/**")
                        .filters(f -> f.filter(filter))
                        .uri("http://%s:8083".formatted(photoApiHost)))
                .route("geojson-generator", r -> r.path("/doc/geojson-generator/**")
                        .filters(f -> f.filter(filter))
                        .uri("http://%s:8088".formatted(geojsonGeneratorHost)))
                .route("notification-api", r -> r.path( "/doc/notification-api/**")
                        .uri("http://%s:8084".formatted(notificationApiHost)))
                .build();
    }
}

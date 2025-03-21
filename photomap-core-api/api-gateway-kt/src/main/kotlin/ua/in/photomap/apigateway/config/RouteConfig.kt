package ua.`in`.photomap.apigateway.config

import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ua.`in`.photomap.apigateway.filter.AuthenticationFilter

@Configuration
class RouteConfig(
    private val authFilter: AuthenticationFilter,
    private val apiProperties: ApiProperties
) {
    companion object {
        private const val URI_TEMPLATE = "http://%s:%d"

        private val USER_API_PATHS = arrayOf(
            "/api/auth/**",
            "/api/user/**",
            "/doc/user-api/**"
        )

        private val PHOTO_API_PATHS = arrayOf(
            "/api/photo/**",
            "/doc/photo-api/**"
        )

        private val GEOJSON_GENERATOR_PATHS = arrayOf(
            "/doc/geojson-generator/**"
        )

        private val NOTIFICATION_API_PATHS = arrayOf(
            "/doc/notification-api/**"
        )
    }

    @Bean
    fun routes(builder: RouteLocatorBuilder): RouteLocator = builder.routes()
        .route("user-api") { r ->
            r.path(*USER_API_PATHS)
                .filters { f -> f.filter(authFilter) }
                .uri(getUri(apiProperties.user))
        }
        .route("photo-api") { r ->
            r.path(*PHOTO_API_PATHS)
                .filters { f -> f.filter(authFilter) }
                .uri(getUri(apiProperties.photo))
        }
        .route("geojson-generator") { r ->
            r.path(*GEOJSON_GENERATOR_PATHS)
                .filters { f -> f.filter(authFilter) }
                .uri(getUri(apiProperties.geojson))
        }
        .route("notification-api") { r ->
            r.path(*NOTIFICATION_API_PATHS)
                .filters { f -> f.filter(authFilter) }
                .uri(getUri(apiProperties.notification))
        }
        .build()

    private fun getUri(service: ServiceProperties): String = 
        URI_TEMPLATE.format(service.host, service.port)
} 
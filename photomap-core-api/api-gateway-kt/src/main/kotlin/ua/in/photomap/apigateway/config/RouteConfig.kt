package ua.`in`.photomap.apigateway.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ua.`in`.photomap.apigateway.filter.AuthenticationFilter

@Configuration
class RouteConfig(
    private val authFilter: AuthenticationFilter
) {
    @Value("\${user-api.host}")
    private lateinit var userApiHost: String
    @Value("\${user-api.port}")
    private var userApiPort: Int = 0

    @Value("\${photo-api.host}")
    private lateinit var photoApiHost: String
    @Value("\${photo-api.port}")
    private var photoApiPort: Int = 0

    @Value("\${geojson-generator.host}")
    private lateinit var geojsonGeneratorHost: String
    @Value("\${geojson-generator.port}")
    private var geojsonGeneratorPort: Int = 0

    @Value("\${notification-api.host}")
    private lateinit var notificationApiHost: String
    @Value("\${notification-api.port}")
    private var notificationApiPort: Int = 0

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
                .uri(getUri(userApiHost, userApiPort))
        }
        .route("photo-api") { r ->
            r.path(*PHOTO_API_PATHS)
                .filters { f -> f.filter(authFilter) }
                .uri(getUri(photoApiHost, photoApiPort))
        }
        .route("geojson-generator") { r ->
            r.path(*GEOJSON_GENERATOR_PATHS)
                .filters { f -> f.filter(authFilter) }
                .uri(getUri(geojsonGeneratorHost, geojsonGeneratorPort))
        }
        .route("notification-api") { r ->
            r.path(*NOTIFICATION_API_PATHS)
                .filters { f -> f.filter(authFilter) }
                .uri(getUri(notificationApiHost, notificationApiPort))
        }
        .build()

    private fun getUri(host: String, port: Int): String = 
        URI_TEMPLATE.format(host, port)
} 
package ua.`in`.photomap.apigateway.config

import lombok.Data
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "api")
@Data
data class ApiProperties(
    val user: ServiceProperties,
    val photo: ServiceProperties,
    val geojson: ServiceProperties,
    val notification: ServiceProperties
)

data class ServiceProperties(
    val host: String,
    val port: Int
) 
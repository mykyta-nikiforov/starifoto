package ua.`in`.photomap.apigateway

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Import
import ua.`in`.photomap.common.cache.config.CacheManagerConfigReference

@SpringBootApplication
@EnableCaching
@Import(CacheManagerConfigReference::class)
@OpenAPIDefinition(info = Info(title = "API Gateway", version = "1.0"))
class ApiGatewayApplication {
    companion object {
        private val log = LoggerFactory.getLogger(ApiGatewayApplication::class.java)
    }
}

fun main(args: Array<String>) {
    runApplication<ApiGatewayApplication>(*args)
} 
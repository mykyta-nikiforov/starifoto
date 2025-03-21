package ua.`in`.photomap.apigateway.filter

import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.core.Ordered
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import ua.`in`.photomap.apigateway.util.AuthUtils
import org.springframework.http.server.reactive.ServerHttpRequest

@Component
class ReactiveMdcFilter(
    private val authUtils: AuthUtils
) : GlobalFilter, Ordered {

    companion object {
        private val log = LoggerFactory.getLogger(ReactiveMdcFilter::class.java)
        private const val EMPTY_AUTH = "emptyAuth"
    }

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        val contextMap = createMdcContext(exchange)
        val startTime = System.nanoTime()

        MDC.setContextMap(contextMap)
        log.info("Request-processing-started")
        MDC.clear()

        return chain.filter(exchange)
            .doOnSuccess {
                val duration = System.nanoTime() - startTime
                MDC.setContextMap(contextMap)
                log.info("Request-processing-finished in: {}", formatDuration(duration))
                MDC.clear()
            }
            .doOnError { error ->
                val duration = System.nanoTime() - startTime
                MDC.setContextMap(contextMap)
                log.error("Request-processing-failed in: {}", formatDuration(duration), error)
                MDC.clear()
            }
    }

    private fun createMdcContext(exchange: ServerWebExchange): Map<String, String> {
        val request = exchange.request
        return mapOf(
            "requestId" to generateRequestId(),
            "httpMethod" to request.method.name(),
            "uri" to request.path.toString(),
            "userEmail" to extractUserEmail(request)
        )
    }

    private fun extractUserEmail(request: ServerHttpRequest): String =
        authUtils.getAuthToken(request)
            ?.let { token ->
                try {
                    authUtils.getDecodedJWT(token)
                        .getClaim("sub")
                        .asString()
                } catch (e: Exception) {
                    EMPTY_AUTH
                }
            } ?: EMPTY_AUTH

    private fun generateRequestId(): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..10)
            .map { allowedChars.random() }
            .joinToString("")
    }

    private fun formatDuration(nanos: Long): String =
        String.format("%.2f ms", nanos / 1_000_000.0)

    override fun getOrder(): Int = -1
} 
package ua.`in`.photomap.apigateway.util

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Component

@Component
class AuthUtils {
    companion object {
        private val log = LoggerFactory.getLogger(AuthUtils::class.java)
        private const val AUTH_HEADER = "Authorization"
        private const val AUTH_HEADER_PREFIX = "Bearer "
        private const val ACCESS_TOKEN_COOKIE = "accessToken"
    }

    @Value("\${photomap.security.secret}")
    private lateinit var jwtSecret: String

    fun getAuthToken(request: ServerHttpRequest): String? =
        getAuthHeaderToken(request) ?: CookieUtils.getCookieValue(request, ACCESS_TOKEN_COOKIE)

    fun getDecodedJWT(token: String): DecodedJWT = try {
        JWT.require(Algorithm.HMAC512(jwtSecret))
            .build()
            .verify(token)
    } catch (e: Exception) {
        log.warn("Failed to decode JWT token: {}", e.message)
        throw e
    }

    private fun getAuthHeaderToken(request: ServerHttpRequest): String? =
        request.headers.getFirst(AUTH_HEADER)
            ?.takeIf { it.startsWith(AUTH_HEADER_PREFIX) }
            ?.substring(AUTH_HEADER_PREFIX.length)
} 
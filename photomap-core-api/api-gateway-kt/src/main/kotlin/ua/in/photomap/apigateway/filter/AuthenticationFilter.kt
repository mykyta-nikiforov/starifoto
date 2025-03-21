package ua.`in`.photomap.apigateway.filter

import com.auth0.jwt.interfaces.DecodedJWT
import org.slf4j.LoggerFactory
import org.springframework.cache.CacheManager
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import ua.`in`.photomap.apigateway.service.UserService
import ua.`in`.photomap.apigateway.util.AuthUtils
import ua.`in`.photomap.apigateway.util.CookieUtils

@Component
class AuthenticationFilter(
    private val userService: UserService,
    private val authUtils: AuthUtils,
    private val cacheManager: CacheManager
) : GatewayFilter {

    companion object {
        private val log = LoggerFactory.getLogger(AuthenticationFilter::class.java)
        private val IGNORED_PATHS = listOf(
            "/api/auth/google",
            "/api/auth/login"
        )
    }

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        log.debug("Auth filter start.")
        val request = exchange.request

        if (!requiresAuthentication(request.uri.path)) {
            return chain.filter(exchange)
        }

        return try {
            authUtils.getAuthToken(request)?.let { token ->
                val jwt = authUtils.getDecodedJWT(token)
                validateToken(exchange, chain, jwt)
            } ?: onError(exchange, HttpStatus.FORBIDDEN)
        } catch (e: Exception) {
            log.warn("Auth filter caught exception: {}", e.message)
            onError(exchange, HttpStatus.FORBIDDEN)
        }
    }

    private fun validateToken(
        exchange: ServerWebExchange,
        chain: GatewayFilterChain,
        jwt: DecodedJWT
    ): Mono<Void> {
        val userId = jwt.getClaim("user_id").asLong()
        val tokenPrivileges = jwt.getClaim("privileges").asList(String::class.java).toSet()

        log.debug("Start auth check. User id: {}", userId)

        return when {
            !getIsEnabled(userId) -> {
                log.debug("User {} is not enabled", userId)
                onError(exchange, HttpStatus.I_AM_A_TEAPOT)
            }
            !validateUserAccess(userId, tokenPrivileges) -> {
                log.debug("User {} access validation failed", userId)
                onError(exchange, HttpStatus.FORBIDDEN)
            }
            else -> chain.filter(exchange)
        }
    }

    private fun validateUserAccess(userId: Long, tokenPrivileges: Set<String>): Boolean {
        val isNonLocked = getIsNonLockedUser(userId)
        log.debug("User {} is non locked: {}", userId, isNonLocked)
        
        if (!isNonLocked) return false
        
        val userPrivileges = getUserPrivileges(userId)
        log.debug("User {} privileges match: {}", userId, tokenPrivileges == userPrivileges)
        
        return tokenPrivileges == userPrivileges
    }

    private fun requiresAuthentication(path: String): Boolean =
        IGNORED_PATHS.none { path.contains(it) }

    private fun getIsEnabled(userId: Long): Boolean =
        getCachedValue("isEnabledUser", userId) {
            userService.isEnabledUser(userId)
        } ?: false

    private fun getIsNonLockedUser(userId: Long): Boolean =
        getCachedValue("isNonLockedUser", userId) {
            userService.isNonLockedUser(userId)
        } ?: false

    private fun getUserPrivileges(userId: Long): Set<String> =
        getCachedValue("getUserPrivileges", userId) {
            userService.getUserPrivileges(userId)
        } ?: emptySet()

    private fun <T> getCachedValue(cacheName: String, key: Any, supplier: () -> T): T? {
        val cache = cacheManager.getCache(cacheName) ?: return supplier()
        
        return cache.get(key)?.get() as? T ?: supplier().also { result ->
            if (result != null) {
                cache.put(key, result)
                log.debug("{} value cached for key {}", cacheName, key)
            }
        }
    }

    private fun onError(exchange: ServerWebExchange, status: HttpStatus): Mono<Void> {
        val response = exchange.response
        response.statusCode = status
        CookieUtils.clearCookie(response, "accessToken")
        return response.setComplete()
    }
} 
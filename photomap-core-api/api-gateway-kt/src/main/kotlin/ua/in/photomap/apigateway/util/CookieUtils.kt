package ua.`in`.photomap.apigateway.util

import org.springframework.boot.web.server.Cookie
import org.springframework.http.ResponseCookie
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository

object CookieUtils {
    fun clearCookie(response: ServerHttpResponse, cookieName: String) {
        ResponseCookie.from(cookieName, "")
            .path("/")
            .maxAge(0)
            .build()
            .also { response.addCookie(it) }
    }

    fun getCookieValue(request: ServerHttpRequest, cookieName: String): String? =
        request.cookies.getFirst(cookieName)
            ?.takeIf { it.value.isNotEmpty() }
            ?.value

    fun createCsrfTokenRepository(): CookieServerCsrfTokenRepository =
        CookieServerCsrfTokenRepository().apply {
            setCookieCustomizer { cookie ->
                cookie.sameSite(Cookie.SameSite.LAX.attributeValue())
                cookie.secure(true)
                cookie.httpOnly(false)
                cookie.path("/")
            }
        }
} 
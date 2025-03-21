package ua.`in`.photomap.apigateway.controller

import org.springframework.security.web.server.csrf.CsrfToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class CsrfController {
    @GetMapping("/csrf")
    fun csrf(token: Mono<CsrfToken>): Mono<Map<String, String>> =
        token.map { csrf -> mapOf("token" to csrf.token) }
} 
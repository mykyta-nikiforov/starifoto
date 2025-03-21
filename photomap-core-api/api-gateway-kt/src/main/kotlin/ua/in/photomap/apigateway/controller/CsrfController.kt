package ua.`in`.photomap.apigateway.controller

import org.springframework.security.web.server.csrf.CsrfToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class CsrfController {
    @GetMapping("/api/csrf")
    fun csrf(): Mono<Map<String, String>> =
        Mono.just(mapOf("result" to "csrf"))
} 
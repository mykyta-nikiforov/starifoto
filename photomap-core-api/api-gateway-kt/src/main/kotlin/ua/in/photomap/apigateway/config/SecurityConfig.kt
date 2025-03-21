package ua.`in`.photomap.apigateway.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager
import org.springframework.security.config.Customizer
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.csrf.CsrfToken
import org.springframework.security.web.server.csrf.ServerCsrfTokenRequestAttributeHandler
import org.springframework.web.server.WebFilter
import reactor.core.publisher.Mono
import ua.`in`.photomap.apigateway.util.CookieUtils

@Configuration
class SecurityConfig {
    @Value("\${swagger.security.username}")
    private lateinit var swaggerUsername: String

    @Value("\${swagger.security.password}")
    private lateinit var swaggerPassword: String

    companion object {
        private val SWAGGER_PATHS = arrayOf(
            "/swagger-ui.html",
            "/webjars/swagger-ui/**"
        )
        private val PUBLIC_PATHS = arrayOf(
            "/doc/**",
            "/csrf"
        )
    }

    @Bean
    fun filterChain(http: ServerHttpSecurity): SecurityWebFilterChain = http
        .authenticationManager(reactiveAuthenticationManager())
        .httpBasic(Customizer.withDefaults())
        .authorizeExchange { exchanges ->
            exchanges
                .pathMatchers(*SWAGGER_PATHS).authenticated()
                .pathMatchers(*PUBLIC_PATHS).permitAll()
                .anyExchange().permitAll()
        }
        .csrf { csrf ->
            csrf
                .csrfTokenRepository(CookieUtils.createCsrfTokenRepository())
                .csrfTokenRequestHandler(ServerCsrfTokenRequestAttributeHandler())
        }
        .build()

    @Bean
    fun reactiveAuthenticationManager(): ReactiveAuthenticationManager =
        UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService())

    @Bean
    fun userDetailsService(): MapReactiveUserDetailsService {
        val encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()
        val admin = User.withUsername(swaggerUsername)
            .password(encoder.encode(swaggerPassword))
            .roles("ADMIN")
            .build()
        return MapReactiveUserDetailsService(admin)
    }

    @Bean
    fun addCsrfTokenFilter(): WebFilter = WebFilter { exchange, chain ->
        val csrfToken = exchange.getAttribute<Mono<CsrfToken>>(CsrfToken::class.java.name)
        csrfToken?.then(chain.filter(exchange)) ?: chain.filter(exchange)
    }
} 
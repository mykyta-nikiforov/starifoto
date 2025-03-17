package ua.in.photomap.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.csrf.*;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

@Configuration
public class SecurityConfig {
    @Value("${swagger.security.username}")
    private String swaggerUsername;

    @Value("${swagger.security.password}")
    private String swaggerPassword;

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        return http
                .authenticationManager(reactiveAuthenticationManager())
                .httpBasic(Customizer.withDefaults())
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/swagger-ui.html", "/webjars/swagger-ui/**").authenticated()
                        .pathMatchers("/doc/**", "/csrf").permitAll()
                        .anyExchange().permitAll())
                .csrf((csrf) -> csrf
                        .csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new SpaServerCsrfTokenRequestHandler())
                )
                .build();
    }

    @Bean
    ReactiveAuthenticationManager reactiveAuthenticationManager() {
        return new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService());
    }

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        UserDetails admin = User.withUsername(swaggerUsername)
                .password(encoder.encode(swaggerPassword))
                .roles("ADMIN")
                .build();
        return new MapReactiveUserDetailsService(admin);
    }

    @Bean
    public WebFilter addCsrfTokenFilter() {
        return (exchange, next) -> Mono.just(exchange)
                .flatMap(ex -> ex.<Mono<CsrfToken>>getAttribute(CsrfToken.class.getName()))
                .then(next.filter(exchange));
    }
}

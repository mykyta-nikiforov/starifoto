package ua.in.photomap.apigateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.Cookie;
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
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.csrf.*;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private static final String[] SWAGGER_PATHS = {"/swagger-ui.html", "/webjars/swagger-ui/**"};
    private static final String[] PUBLIC_PATHS = {"/doc/**", "/csrf"};

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
                        .pathMatchers(SWAGGER_PATHS).authenticated()
                        .pathMatchers(PUBLIC_PATHS).permitAll()
                        .anyExchange().permitAll())
                .csrf((csrf) -> csrf
                        .csrfTokenRepository(getCookiesCsrfTokenRepository())
                        .csrfTokenRequestHandler(new ServerCsrfTokenRequestAttributeHandler())
                )
                .build();
    }

    public static CookieServerCsrfTokenRepository getCookiesCsrfTokenRepository() {
        final CookieServerCsrfTokenRepository repository = new CookieServerCsrfTokenRepository();
        repository.setCookieCustomizer((x) -> {
            x.sameSite(Cookie.SameSite.LAX.attributeValue());
            x.secure(true);
            x.httpOnly(false);
        });
        return repository;
    }

    @Bean
    ReactiveAuthenticationManager reactiveAuthenticationManager() {
        return new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService());
    }

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        UserDetails admin = User.withUsername(swaggerUsername)
                .password(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(swaggerPassword))
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

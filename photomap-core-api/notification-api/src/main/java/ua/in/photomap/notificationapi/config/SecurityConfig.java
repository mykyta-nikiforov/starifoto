package ua.in.photomap.notificationapi.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import ua.in.photomap.common.rest.toolkit.util.JwtService;
import ua.in.photomap.notificationapi.filter.NotificationApiAuthFilter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, NotificationApiAuthFilter notificationApiAuthFilter) throws Exception {
        http
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/api/notification/**").permitAll()
                        .requestMatchers("/health/**").permitAll()
                        .requestMatchers("/ws").permitAll()
                        .requestMatchers("/doc/notification-api/**").permitAll()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .addFilterBefore(notificationApiAuthFilter, BasicAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public NotificationApiAuthFilter notificationApiAuthFilter(JwtService jwtService) {
        return new NotificationApiAuthFilter(jwtService);
    }
}

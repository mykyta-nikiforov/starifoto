package ua.in.photomap.common.rest.toolkit.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ua.in.photomap.common.rest.toolkit.filter.JwtTokenAuthFilter;
import ua.in.photomap.common.rest.toolkit.util.JwtService;

@Configuration
public class SecurityCommonConfig {

    @Bean
    @ConditionalOnMissingBean
    public JwtTokenAuthFilter tokenAuthFilter(JwtService jwtService) {
        return new JwtTokenAuthFilter(jwtService);
    }
}
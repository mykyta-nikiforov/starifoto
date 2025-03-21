package ua.in.photomap.geojsongenerator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class RestConfig {
    @Bean
    public WebClient webClient() {
        return WebClient.create();
    }
}

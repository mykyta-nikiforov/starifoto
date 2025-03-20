package ua.in.photomap.apigateway;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;
import ua.in.photomap.common.cache.config.CacheManagerConfigReference;

@SpringBootApplication
@RequiredArgsConstructor
@EnableCaching
@Import(CacheManagerConfigReference.class)
@OpenAPIDefinition(info = @Info(title = "API Gateway", version = "1.0"))
@Slf4j
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}

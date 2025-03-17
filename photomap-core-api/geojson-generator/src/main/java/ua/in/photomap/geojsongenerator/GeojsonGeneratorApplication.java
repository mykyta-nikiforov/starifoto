package ua.in.photomap.geojsongenerator;

import io.mongock.runner.springboot.EnableMongock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import ua.in.photomap.common.swagger.SwaggerConfigReference;

@SpringBootApplication
@EnableMongock
@Import({SwaggerConfigReference.class})
public class GeojsonGeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(GeojsonGeneratorApplication.class, args);
    }

}

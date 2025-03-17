package ua.in.photomap.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import ua.in.photomap.common.cache.config.CacheManagerConfigReference;
import ua.in.photomap.common.rest.toolkit.RestToolkitConfigReference;
import ua.in.photomap.common.swagger.SwaggerConfigReference;

@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
@Import({RestToolkitConfigReference.class, CacheManagerConfigReference.class, SwaggerConfigReference.class})

public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

}

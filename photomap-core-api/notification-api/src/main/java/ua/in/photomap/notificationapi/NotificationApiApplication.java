package ua.in.photomap.notificationapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import ua.in.photomap.common.rest.toolkit.RestToolkitConfigReference;
import ua.in.photomap.common.swagger.SwaggerConfigReference;

@SpringBootApplication
@Import({RestToolkitConfigReference.class, SwaggerConfigReference.class})
public class NotificationApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationApiApplication.class, args);
	}

}

package ua.in.photomap.photoapi.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    public static final String PHOTO_ADD_TOPIC = "photo-add-topic";

    public static final String PHOTO_UPDATE_TOPIC = "photo-update-topic";

    public static final String PHOTO_DELETE_TOPIC = "photo-delete-topic";

    @Bean
    public NewTopic photoAddTopic() {
        return TopicBuilder.name(PHOTO_ADD_TOPIC).build();
    }

    @Bean
    public NewTopic photoDeleteTopic() {
        return TopicBuilder.name(PHOTO_DELETE_TOPIC).build();
    }

    @Bean
    public NewTopic photoUpdateTopic() {
        return TopicBuilder.name(PHOTO_UPDATE_TOPIC).build();
    }
}

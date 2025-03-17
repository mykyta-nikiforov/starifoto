package ua.in.photomap.geojsongenerator.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public KafkaReceiver<Integer, String> photoAddKafkaReceiver() {
        return KafkaReceiver.create(photoAddReceiverOptions());
    }

    @Bean
    public KafkaReceiver<Integer, String> photoDeleteKafkaReceiver() {
        return KafkaReceiver.create(photoDeleteReceiverOptions());
    }

    @Bean
    public KafkaReceiver<Integer, String> photoUpdateKafkaReceiver() {
        return KafkaReceiver.create(photoUpdateReceiverOptions());
    }

    @Bean
    public ReceiverOptions<Integer, String> photoAddReceiverOptions() {
        return createReceiverOptions("photo-add-topic");
    }

    @Bean
    public ReceiverOptions<Integer, String> photoDeleteReceiverOptions() {
        return createReceiverOptions("photo-delete-topic");
    }

    @Bean
    public ReceiverOptions<Integer, String> photoUpdateReceiverOptions() {
        return createReceiverOptions("photo-update-topic");
    }


    private ReceiverOptions<Integer, String> createReceiverOptions(String topic) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "photomap-group");
        return ReceiverOptions.<Integer, String>create(props).subscription(Collections.singleton(topic));
    }
}

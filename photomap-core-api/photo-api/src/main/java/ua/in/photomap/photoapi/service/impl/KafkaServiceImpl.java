package ua.in.photomap.photoapi.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ua.in.photomap.common.photo.model.dto.PhotoGeoJsonDataDTO;
import ua.in.photomap.photoapi.config.KafkaTopicConfig;
import ua.in.photomap.photoapi.service.KafkaService;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaServiceImpl implements KafkaService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void pushNewPhotoEvent(PhotoGeoJsonDataDTO photo) {
        log.info("Pushing new photo event to Kafka. Photo Id: {}", photo.getPhotoId());
        try {
            String photoAsMessage = objectMapper.writeValueAsString(photo);
            sendMessage(KafkaTopicConfig.PHOTO_ADD_TOPIC, photoAsMessage);
        } catch (JsonProcessingException e) {
            log.error("Error while preparing message for Kafka. Photo object: {}", photo, e);
        }
    }

    @Override
    public void pushUpdatePhotoEvent(PhotoGeoJsonDataDTO photoDTO) {
        log.info("Pushing update photo event to Kafka. Photo Id: {}", photoDTO.getPhotoId());
        try {
            String photoAsMessage = objectMapper.writeValueAsString(photoDTO);
            sendMessage(KafkaTopicConfig.PHOTO_UPDATE_TOPIC, photoAsMessage);
        } catch (JsonProcessingException e) {
            log.error("Error while preparing message for Kafka. Photo object: {}", photoDTO, e);
        }
    }

    @Override
    public void pushDeletePhotoEvent(Long photoId) {
        log.info("Pushing delete photo event to Kafka. Photo Id: {}", photoId);
        sendMessage(KafkaTopicConfig.PHOTO_DELETE_TOPIC, String.valueOf(photoId));
    }

    private void sendMessage(String topic, String messageBody) {
        CompletableFuture.runAsync(() -> {
            log.info("Sending message to Kafka. Topic: {}, message: {}", topic, messageBody);
            kafkaTemplate.send(topic, messageBody);
        });
    }
}


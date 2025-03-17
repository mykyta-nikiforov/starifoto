package ua.in.photomap.photoapi.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ua.in.photomap.common.photo.model.dto.websocket.UserPhotoNotificationDTO;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationSender {

    private final RestTemplate restTemplate;

    @Value("${photomap.notification-api.base-url}")
    private String notificationApiBaseUrl;

    public void sendUserPhotoNotification(UserPhotoNotificationDTO message) {
        ResponseEntity<String> response = restTemplate.postForEntity(notificationApiBaseUrl + "/ws/user/photo", message, String.class);
        log.info("Notification sent. Response: " + response);
    }
}

package ua.in.photomap.notificationapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.in.photomap.common.photo.model.dto.websocket.UserPhotoNotificationDTO;
import ua.in.photomap.notificationapi.service.RedisPublishService;

@RestController
@RequestMapping("/api/notification/ws")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "WebSocket Notification API", description = "API for sending WebSocket notifications.")
public class NotificationController {
    private final RedisPublishService redisPublishService;

    @PostMapping("/user/photo")
    @Operation(summary = "Send WebSocket notification about user photo update.")
    public ResponseEntity<?> sendUserPhotoNotification(
            @Parameter(description = "UserId and photoId", required = true)
            @RequestBody UserPhotoNotificationDTO message) {
        log.info("Sending user notification: {}", message);
        redisPublishService.publishUserPhotoNotification(message);
        return ResponseEntity.ok().build();
    }
}

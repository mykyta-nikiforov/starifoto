package ua.in.photomap.notificationapi.service;

import ua.in.photomap.common.photo.model.dto.websocket.UserPhotoNotificationDTO;

public interface RedisPublishService {
    void publishUserPhotoNotification(UserPhotoNotificationDTO message);
}

package ua.in.photomap.notificationapi.config;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import ua.in.photomap.common.photo.model.dto.websocket.UserPhotoNotificationDTO;
import ua.in.photomap.notificationapi.service.WebSocketNotifier;

/**
 * Listens on Redis topic and sends notifications to users connected to WebSocket on this node.
 */
@RequiredArgsConstructor
@Component
public class RedisTopicListeners implements InitializingBean {

    private final WebSocketNotifier webSocketNotifier;

    private final RedissonClient redissonClient;

    @Override
    public void afterPropertiesSet() {
        RTopic broadcastRTopic = redissonClient.getTopic(RedisTopic.USER_PHOTO_NOTIFICATION.getTopicName());
        broadcastRTopic.addListener(UserPhotoNotificationDTO.class, (systemBroadcastTopic, message) -> webSocketNotifier.sendUserPhotoNotification(message));
    }
}

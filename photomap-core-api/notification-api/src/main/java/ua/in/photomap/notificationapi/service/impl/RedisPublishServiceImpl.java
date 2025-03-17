package ua.in.photomap.notificationapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import ua.in.photomap.common.photo.model.dto.websocket.UserPhotoNotificationDTO;
import ua.in.photomap.notificationapi.config.RedisTopic;
import ua.in.photomap.notificationapi.service.RedisPublishService;

@RequiredArgsConstructor
@Service
public class RedisPublishServiceImpl implements RedisPublishService {
    private final RedissonClient redissonClient;

    @Override
    public void publishUserPhotoNotification(UserPhotoNotificationDTO message) {
        publishMessage(RedisTopic.USER_PHOTO_NOTIFICATION, message);
    }

    private void publishMessage(RedisTopic topicName, Object message) {
        RTopic topic = redissonClient.getTopic(topicName.getTopicName());
        topic.publish(message);
    }
}

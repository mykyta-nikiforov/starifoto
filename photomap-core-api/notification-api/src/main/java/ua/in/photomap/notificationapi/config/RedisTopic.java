package ua.in.photomap.notificationapi.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RedisTopic {
    USER_PHOTO_NOTIFICATION("user-photo-notification");

    private final String topicName;
}

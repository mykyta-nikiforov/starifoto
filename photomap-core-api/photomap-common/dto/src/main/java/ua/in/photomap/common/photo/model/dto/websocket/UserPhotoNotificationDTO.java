package ua.in.photomap.common.photo.model.dto.websocket;

import lombok.*;
import ua.in.photomap.common.photo.model.constant.UserPhotoNotificationType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPhotoNotificationDTO {
    private Long userId;
    private Long photoId;
    private UserPhotoNotificationType type;
}

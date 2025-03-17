package ua.in.photomap.notificationapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import ua.in.photomap.common.photo.model.dto.websocket.UserPhotoNotificationDTO;
import ua.in.photomap.notificationapi.service.WebSocketNotifier;

@Service
@RequiredArgsConstructor
public class WebSocketNotifierImpl implements WebSocketNotifier {

    private final SimpMessagingTemplate websocket;


    @Override
    public void sendUserPhotoNotification(UserPhotoNotificationDTO message) {
        String url = "/topic/user/" + message.getUserId();
        websocket.convertAndSend(url, message);
    }
}

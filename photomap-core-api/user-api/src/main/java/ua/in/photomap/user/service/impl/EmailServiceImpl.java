package ua.in.photomap.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ua.in.photomap.common.photo.model.dto.email.AuthTokenizedActionEmailDTO;
import ua.in.photomap.user.service.EmailService;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final RestTemplate restTemplate;

    @Value("${photomap.notification-api.base-url}")
    private String notificationApiBaseUrl;

    @Override
    public void sendConfirmEmail(AuthTokenizedActionEmailDTO message) {
        restTemplate.postForEntity(notificationApiBaseUrl + "/email/confirm", message, String.class);
    }

    @Override
    public void sendResetPasswordEmail(AuthTokenizedActionEmailDTO message) {
        restTemplate.postForEntity(notificationApiBaseUrl + "/email/password/reset", message, String.class);
    }
}

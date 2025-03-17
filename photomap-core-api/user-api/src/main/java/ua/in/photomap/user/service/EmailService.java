package ua.in.photomap.user.service;

import ua.in.photomap.common.photo.model.dto.email.AuthTokenizedActionEmailDTO;

public interface EmailService {
    void sendConfirmEmail(AuthTokenizedActionEmailDTO message);

    void sendResetPasswordEmail(AuthTokenizedActionEmailDTO message);
}

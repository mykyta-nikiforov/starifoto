package ua.in.photomap.notificationapi.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import ua.in.photomap.common.photo.model.dto.email.AuthTokenizedActionEmailDTO;
import ua.in.photomap.common.rest.toolkit.exception.InternalException;
import ua.in.photomap.notificationapi.service.EmailService;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final SpringTemplateEngine templateEngine;
    private final JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String emailFrom;

    @Value("${photomap.client.url}")
    private String clientUrl;

    private final static String CONFIRM_EMAIL_SUBJECT = "СтаріФото.Укр — Підтвердження електронної пошти";
    private final static String RESET_PASSWORD_SUBJECT = "СтаріФото.Укр — Скинути пароль";

    @Override
    public void sendConfirmEmail(AuthTokenizedActionEmailDTO dto) {
        final Context ctx = new Context();
        ctx.setVariable("username", dto.getUsername());
        ctx.setVariable("confirmLink", clientUrl + "/confirm-email?token=" + dto.getToken());
        final String htmlContent = templateEngine.process("confirm-email", ctx);
        sendEmail(dto.getEmail(), htmlContent, CONFIRM_EMAIL_SUBJECT);
    }

    @Override
    public void sendResetPasswordEmail(AuthTokenizedActionEmailDTO message) {
        final Context ctx = new Context();
        ctx.setVariable("username", message.getUsername());
        ctx.setVariable("resetPasswordLink", clientUrl + "/reset-password-confirm?token=" + message.getToken());
        final String htmlContent = templateEngine.process("reset-password-email", ctx);
        sendEmail(message.getEmail(), htmlContent, RESET_PASSWORD_SUBJECT);
    }

    private void sendEmail(String emailTo, String htmlContent, String subject) {
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");
            helper.setFrom(emailFrom);
            helper.setTo(emailTo);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
        } catch (MessagingException e) {
            throw new InternalException("Failed to send email", e);
        }
        emailSender.send(mimeMessage);
    }
}

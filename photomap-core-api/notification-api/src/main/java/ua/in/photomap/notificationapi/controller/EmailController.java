package ua.in.photomap.notificationapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.in.photomap.common.photo.model.dto.email.AuthTokenizedActionEmailDTO;
import ua.in.photomap.notificationapi.service.EmailService;

@RestController
@RequestMapping("/api/notification/email")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Email Notification API", description = "API for sending email notifications.")
public class EmailController {
    private final EmailService emailService;

    @PostMapping("/confirm")
    @Operation(summary = "Send user confirm email.")
    public ResponseEntity<?> sendUserConfirmNotification(
            @Parameter(description = "User's email, token, username", required = true)
            @RequestBody @Valid AuthTokenizedActionEmailDTO message) {
        log.info("Sending user confirm email to {}", message.getEmail());
        emailService.sendConfirmEmail(message);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password/reset")
    @Operation(summary = "Send reset password email.")
    public ResponseEntity<?> sendUserResetPasswordEmail(
            @Parameter(description = "User's email, token, username", required = true)
            @RequestBody @Valid AuthTokenizedActionEmailDTO message) {
        log.info("Sending user reset password email to {}", message.getEmail());
        emailService.sendResetPasswordEmail(message);
        return ResponseEntity.ok().build();
    }
}

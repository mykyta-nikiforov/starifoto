package ua.in.photomap.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.in.photomap.user.dto.*;
import ua.in.photomap.user.model.User;
import ua.in.photomap.user.security.UserResolver;
import ua.in.photomap.user.service.AuthService;
import ua.in.photomap.user.service.CookiesService;
import ua.in.photomap.user.service.GoogleAuthService;
import ua.in.photomap.user.validator.AuthValidator;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Auth API", description = "API for user authentication")
public class AuthController {
    private final GoogleAuthService googleAuthService;
    private final AuthService authService;
    private final CookiesService cookiesService;
    private final AuthValidator authValidator;

    @PostMapping("/sign-up")
    @Operation(summary = "Sign up user.")
    public ResponseEntity<AuthResponse> signUp(@Parameter(description = "Sign up request", required = true)
                                               @RequestBody @Valid SignUpRequest request) {
        authValidator.validateSignUpRequest(request);
        authService.signUp(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/email/confirm")
    @Operation(summary = "Confirm email.")
    public ResponseEntity<Void> confirmEmail(
            @Parameter(description = "Confirm email request", required = true)
            @RequestBody @Valid ConfirmEmailRequest request) {
        authService.confirmEmail(request.getToken());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password/reset")
    @Operation(summary = "Request reset password.")
    public ResponseEntity<Void> requestResetPassword(
            @Parameter(description = "Request reset password email", required = true)
            @RequestBody @Valid ResetPasswordRequest request) {
        authService.requestResetPassword(request.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password/reset/confirm")
    @Operation(summary = "Reset password.")
    public ResponseEntity<Void> resetPassword(
            @Parameter(description = "Reset password request", required = true)
            @RequestBody @Valid ResetPasswordConfirmRequest request) {
        authService.resetPassword(request.getToken(), request.getPassword());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    @Operation(summary = "Login user.")
    public ResponseEntity<AuthResponse> login(
            @Parameter(description = "Login request", required = true)
            @RequestBody @Valid LoginRequest request, HttpServletResponse response) {
        AuthResponse authResponse = authService.login(request);
        cookiesService.addTokenCookies(response, authResponse.getAccessToken(), authResponse.getRefreshToken());
        return ResponseEntity.ok()
                .body(authResponse);
    }

    @PostMapping("/google")
    @Operation(summary = "Authenticate user with Google.")
    public ResponseEntity<AuthResponse> authenticate(
            @Parameter(description = "Google credential", required = true)
            @RequestBody GoogleAuthRequest request, HttpServletResponse response) {
        log.debug("Google auth request: {}", request);
        User user = googleAuthService.getUserInfo(request.getCredential());
        AuthResponse authResponse = authService.authenticate(user);

        cookiesService.addTokenCookies(response, authResponse.getAccessToken(), authResponse.getRefreshToken());
        return ResponseEntity.ok()
                .body(authResponse);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token.")
    public ResponseEntity<AuthResponse> refresh(HttpServletRequest request, HttpServletResponse response) {
        log.debug("Refresh token for user: {}", UserResolver.getCurrentUser());
        AuthResponse authResponse = authService.refresh(request);
        cookiesService.addTokenCookies(response, authResponse.getAccessToken(), authResponse.getRefreshToken());
        return ResponseEntity.ok()
                .body(authResponse);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user. Clear cookies.")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        cookiesService.removeRefreshTokenCookie(response);
        return ResponseEntity.ok().build();
    }
}

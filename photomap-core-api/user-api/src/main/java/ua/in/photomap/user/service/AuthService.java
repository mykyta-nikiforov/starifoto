package ua.in.photomap.user.service;

import jakarta.servlet.http.HttpServletRequest;
import ua.in.photomap.user.dto.AuthResponse;
import ua.in.photomap.user.dto.LoginRequest;
import ua.in.photomap.user.dto.SignUpRequest;
import ua.in.photomap.user.model.User;

public interface AuthService {
    void signUp(SignUpRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse authenticate(User user);

    AuthResponse refresh(HttpServletRequest refreshToken);

    void confirmEmail(String token);

    void resetPassword(String token, String password);

    void requestResetPassword(String email);
}

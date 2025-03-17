package ua.in.photomap.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.in.photomap.common.rest.toolkit.exception.ResourceNotFoundException;
import ua.in.photomap.user.model.AuthActionToken;
import ua.in.photomap.user.model.AuthActionType;
import ua.in.photomap.user.model.User;
import ua.in.photomap.user.repository.AuthActionTokenRepository;
import ua.in.photomap.user.service.AuthTokenService;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthActionTokenServiceImpl implements AuthTokenService {
    private final AuthActionTokenRepository authActionTokenRepository;

    @Value("${photomap.auth-action-token.expiration-time-in-minutes}")
    private Long tokenExpirationTimeInMinutes;

    @Override
    public AuthActionToken generateToken(User user, AuthActionType type) {
        // Generate a new unique token
        String token = UUID.randomUUID().toString();

        AuthActionToken authActionToken = new AuthActionToken();
        authActionToken.setToken(token);
        authActionToken.setUser(user);
        authActionToken.setExpiresAt(LocalDateTime.now().plusMinutes(tokenExpirationTimeInMinutes));
        authActionToken.setIsUsed(false);
        authActionToken.setType(type);
        return save(authActionToken);
    }

    @Override
    public AuthActionToken getByToken(String token, AuthActionType confirmEmail) {
        return authActionTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token not found"));
    }

    @Override
    public AuthActionToken save(AuthActionToken authActionToken) {
        return authActionTokenRepository.save(authActionToken);
    }
}

package ua.in.photomap.user.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.in.photomap.common.rest.toolkit.exception.ForbiddenException;
import ua.in.photomap.common.rest.toolkit.exception.UnauthorizedException;
import ua.in.photomap.user.model.User;
import ua.in.photomap.user.service.GoogleAuthService;
import ua.in.photomap.user.service.UserService;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleAuthServiceImpl implements GoogleAuthService {
    private final UserService userService;
    private final GoogleIdTokenVerifier googleIdTokenVerifier;

    @Override
    public User getUserInfo(String googleAuthToken) {
        try {
            GoogleIdToken idToken = googleIdTokenVerifier.verify(googleAuthToken);
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();

                // Get profile information from payload
                validateGooglePayload(payload);

                Optional<User> userOptional = userService.findByEmailJoinPrivileges(payload.getEmail());
                validateUser(userOptional);

                return userOptional.orElseGet(() -> createUser(payload));
            } else {
                throw new UnauthorizedException("Invalid ID token.");
            }
        } catch (GeneralSecurityException | IOException e) {
            throw new UnauthorizedException("Invalid ID token.");
        }
    }

    private User createUser(GoogleIdToken.Payload payload) {
        return userService.createGoogleUser((String) payload.get("name"), payload.getEmail());
    }

    private void validateGooglePayload(GoogleIdToken.Payload payload) {
        boolean emailVerified = payload.getEmailVerified();
        if (!emailVerified) {
            throw new UnauthorizedException("Google email is not verified.");
        }
    }

    private static void validateUser(Optional<User> userOptional) {
        if (userOptional.isPresent() &&
                (!userOptional.get().getIsEnabled() || !userOptional.get().getIsNonLocked())) {
            throw new ForbiddenException("User is disabled");
        }
    }
}

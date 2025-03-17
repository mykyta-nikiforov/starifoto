package ua.in.photomap.user.service;

import ua.in.photomap.user.model.AuthActionToken;
import ua.in.photomap.user.model.AuthActionType;
import ua.in.photomap.user.model.User;

public interface AuthTokenService {
    AuthActionToken generateToken(User user, AuthActionType type);

    AuthActionToken getByToken(String token, AuthActionType confirmEmail);

    AuthActionToken save(AuthActionToken authActionToken);
}

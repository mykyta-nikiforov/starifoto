package ua.in.photomap.user.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.in.photomap.common.photo.model.dto.email.AuthTokenizedActionEmailDTO;
import ua.in.photomap.common.rest.toolkit.exception.ForbiddenException;
import ua.in.photomap.common.rest.toolkit.exception.ResourceNotFoundException;
import ua.in.photomap.common.rest.toolkit.exception.UnauthorizedException;
import ua.in.photomap.common.rest.toolkit.exception.ValidationException;
import ua.in.photomap.common.rest.toolkit.util.CookiesUtils;
import ua.in.photomap.user.dto.AuthResponse;
import ua.in.photomap.user.dto.LoginRequest;
import ua.in.photomap.user.dto.SignUpRequest;
import ua.in.photomap.user.dto.UserDTO;
import ua.in.photomap.user.exception.UnconfirmedUserException;
import ua.in.photomap.user.mapper.UserMapper;
import ua.in.photomap.user.model.AuthActionToken;
import ua.in.photomap.user.model.AuthActionType;
import ua.in.photomap.user.model.User;
import ua.in.photomap.user.security.UserResolver;
import ua.in.photomap.user.service.*;
import ua.in.photomap.user.utils.Base64Utils;
import ua.in.photomap.user.utils.JwtUtils;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final EncryptionService encryptionService;
    private final AuthTokenService authTokenService;
    private final EmailService emailService;

    @Override
    public void signUp(SignUpRequest request) {
        String decodedPassword = Base64Utils.decode(request.getPassword());
        User unconfirmedUser = userService.createUnconfirmedUser(request.getUsername(), request.getEmail(),
                decodedPassword);
        AuthActionToken authActionToken = authTokenService.generateToken(unconfirmedUser, AuthActionType.CONFIRM_EMAIL);
        CompletableFuture.runAsync(() -> {
            AuthTokenizedActionEmailDTO emailDTO = new AuthTokenizedActionEmailDTO(unconfirmedUser.getEmail(), authActionToken.getToken(),
                    unconfirmedUser.getUsername());
            emailService.sendConfirmEmail(emailDTO);
        });
    }

    @Override
    @Transactional
    public void confirmEmail(String token) {
        AuthActionToken authActionToken = authTokenService.getByToken(token, AuthActionType.CONFIRM_EMAIL);
        userService.updateIsEnabled(authActionToken.getUser().getId(), true);
        authActionToken.setIsUsed(true);
        authTokenService.save(authActionToken);
    }

    @Override
    @Transactional
    public void resetPassword(String token, String password) {
        AuthActionToken authActionToken = authTokenService.getByToken(token, AuthActionType.RESET_PASSWORD);
        String decodedPassword = Base64Utils.decode(password);

        userService.updatePasswordByUserId(authActionToken.getUser().getId(), decodedPassword);
        authActionToken.setIsUsed(true);
        authTokenService.save(authActionToken);
    }

    @Override
    public void requestResetPassword(String email) {
        User user = userService.findByEmailJoinPrivileges(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        AuthActionToken authActionToken = authTokenService.generateToken(user, AuthActionType.RESET_PASSWORD);
        CompletableFuture.runAsync(() -> {
            AuthTokenizedActionEmailDTO message = new AuthTokenizedActionEmailDTO(user.getEmail(), authActionToken.getToken(),
                    user.getUsername());
            emailService.sendResetPasswordEmail(message);
        });
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userService.findByEmailJoinPrivileges(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        String decodedPassword = Base64Utils.decode(request.getPassword());
        if (!encryptionService.matchPassword(decodedPassword, user.getPassword())) {
            throw new UnauthorizedException("Authentication failed");
        }
        if (!user.getIsEnabled()) {
            throw new UnconfirmedUserException("Email is not confirmed");
        }
        return authenticate(user);
    }

    @Override
    public AuthResponse authenticate(User user) {
        String accessToken = jwtUtils.generateJwt(user);
        String refreshToken = jwtUtils.generateRefreshToken(user.getEmail());
        return buildAuthenticationResponse(user, accessToken, refreshToken);
    }

    @Override
    public AuthResponse refresh(HttpServletRequest request) {
        String currentRefreshToken = CookiesUtils.getCookieByName(request, "refreshToken")
                .orElseThrow(() -> new ValidationException("Refresh token not found"));

        User user = UserResolver.getCurrentUser();
        if (!user.getIsEnabled()) {
            throw new ForbiddenException("User is disabled");
        }

        String accessToken = jwtUtils.generateJwt(user);
        String refreshToken;
        if (jwtUtils.shouldUpdateRefreshToken(currentRefreshToken)) {
            refreshToken = jwtUtils.generateRefreshToken(user.getEmail());
        } else {
            refreshToken = currentRefreshToken;
        }
        return buildAuthenticationResponse(user, accessToken, refreshToken);
    }

    private AuthResponse buildAuthenticationResponse(User user, String accessToken, String refreshToken) {
        UserDTO userDTO = UserMapper.INSTANCE.userToUserDto(user);
        AuthResponse response = new AuthResponse();
        response.setUser(userDTO);
        response.setAccessToken(accessToken);
        response.setExpiresIn(jwtUtils.getTokenExpiresIn(accessToken));
        response.setRefreshToken(refreshToken);
        response.setRefreshTokenExpiresIn(jwtUtils.getTokenExpiresIn(refreshToken));
        return response;
    }
}

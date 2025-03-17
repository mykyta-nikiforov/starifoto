package ua.in.photomap.user.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.in.photomap.user.dto.SignUpRequest;
import ua.in.photomap.user.exception.EmailUsedException;
import ua.in.photomap.user.service.UserService;

@Component
@RequiredArgsConstructor
public class AuthValidator {
    private final UserService userService;

    public void validateSignUpRequest(SignUpRequest request) {
        if (userService.existsByEmail(request.getEmail())) {
            throw new EmailUsedException("Email is used");
        }
    }
}

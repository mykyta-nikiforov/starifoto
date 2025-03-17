package ua.in.photomap.user.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import ua.in.photomap.common.rest.toolkit.exception.ValidationException;
import ua.in.photomap.user.dto.UpdateUserPasswordRequest;
import ua.in.photomap.user.dto.UpdateUserRequest;
import ua.in.photomap.user.model.User;
import ua.in.photomap.user.repository.RoleRepository;
import ua.in.photomap.user.repository.UserRepository;
import ua.in.photomap.user.security.UserResolver;
import ua.in.photomap.user.service.EncryptionService;
import ua.in.photomap.user.utils.Base64Utils;

@Component
@RequiredArgsConstructor
public class UserValidator {
    private final EncryptionService encryptionService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public void validateUpdatePasswordRequest(UpdateUserPasswordRequest request) {
        User currentUser = UserResolver.getCurrentUser();
        boolean noPasswordUser = currentUser.getPassword() == null;

        if (!noPasswordUser) {
            boolean matchPassword = encryptionService.matchPassword(Base64Utils.decode(request.getOldPassword()), currentUser.getPassword());
            if (!matchPassword) {
                throw new ValidationException("Wrong password");
            }
        }
    }

    public void validateUpdateUserRequest(Long userId, UpdateUserRequest dto) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("User not found"));
        if (CollectionUtils.isEmpty(dto.getRolesIds())) {
            throw new ValidationException("Roles are required");
        }
        Long existingRoles = roleRepository.countAllByIds(dto.getRolesIds());
        if (!existingRoles.equals((long) dto.getRolesIds().size())) {
            throw new ValidationException("Some roles not found");
        }
    }
}

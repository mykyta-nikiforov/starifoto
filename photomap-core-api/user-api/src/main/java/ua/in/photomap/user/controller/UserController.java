package ua.in.photomap.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.in.photomap.common.rest.toolkit.annotation.PreAuthorizeModeratorOrHigher;
import ua.in.photomap.common.rest.toolkit.annotation.PreAuthorizeUserOrHigher;
import ua.in.photomap.user.dto.*;
import ua.in.photomap.user.mapper.UserMapper;
import ua.in.photomap.user.model.User;
import ua.in.photomap.user.security.UserResolver;
import ua.in.photomap.user.service.UserService;
import ua.in.photomap.user.utils.Base64Utils;
import ua.in.photomap.user.validator.UserValidator;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Tag(name = "User API", description = "API to get and manage users")
public class UserController {
    private final UserService userService;
    private final UserValidator userValidator;

    @PreAuthorizeUserOrHigher
    @GetMapping("/me")
    public UserDTO getUser() {
        User user = UserResolver.getCurrentUser();
        return UserMapper.INSTANCE.userToUserDto(user);
    }

    @PreAuthorizeUserOrHigher
    @PatchMapping("/me/account")
    @Operation(summary = "Update current user account")
    public ResponseEntity<Void> updateAccount(
            @Parameter(description = "Update user account request", required = true)
            @RequestBody UpdateUserAccountRequest request) {
        userService.updateCurrentUserAccount(request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorizeUserOrHigher
    @PatchMapping("/me/password")
    @Operation(summary = "Update current user password")
    public ResponseEntity<Void> updatePassword(
            @Parameter(description = "Update user password request", required = true)
            @RequestBody UpdateUserPasswordRequest request) {
        userValidator.validateUpdatePasswordRequest(request);
        userService.updateCurrentUserPassword(Base64Utils.decode(request.getNewPassword()));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{userId}/is-enabled")
    @PreAuthorizeModeratorOrHigher
    @Operation(summary = "Update user is enabled or not")
    public ResponseEntity<Void> patchUser(
            @Parameter(description = "User id", required = true)
            @PathVariable Long userId,
            @Parameter(description = "isEnabled boolean", required = true)
            @RequestBody Map<String, Boolean> body) {
        Boolean isEnabled = body.get("isEnabled");
        if (isEnabled == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        userService.updateIsEnabled(userId, isEnabled);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{userId}")
    @PreAuthorizeModeratorOrHigher
    @Operation(summary = "Update user")
    public ResponseEntity<UserDTO> updateUser(
            @Parameter(description = "User id", required = true) @PathVariable Long userId,
            @Parameter(description = "Update user request", required = true) @RequestBody UpdateUserRequest dto) {
        userValidator.validateUpdateUserRequest(userId, dto);
        UserDTO userDto = UserMapper.INSTANCE.userToUserDto(userService.update(userId, dto));
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @GetMapping("/all")
    @PreAuthorizeModeratorOrHigher
    @Operation(summary = "Get all users")
    public Page<UserDTO> getAll(
            @Parameter(description = "Page number", required = true) @RequestParam int page,
            @Parameter(description = "Page size", required = true) @RequestParam int size) {
        return userService.getAll(page, size).map(UserMapper.INSTANCE::userToUserDto);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user by id")
    public UserPublicDTO getUserById(
            @Parameter(description = "User id", required = true) @PathVariable Long userId) {
        return UserMapper.INSTANCE.userToUserPublicDto(userService.getById(userId));
    }
}

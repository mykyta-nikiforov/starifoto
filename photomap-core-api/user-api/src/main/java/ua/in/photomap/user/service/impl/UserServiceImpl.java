package ua.in.photomap.user.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ua.in.photomap.common.photo.model.dto.UserBasicDTO;
import ua.in.photomap.common.photo.model.dto.batch.GetUsersByIdsResponse;
import ua.in.photomap.common.rest.toolkit.exception.InternalException;
import ua.in.photomap.common.rest.toolkit.exception.ResourceNotFoundException;
import ua.in.photomap.common.rest.toolkit.util.DiffUtils;
import ua.in.photomap.user.dto.UpdateUserAccountRequest;
import ua.in.photomap.user.dto.UpdateUserPasswordRequest;
import ua.in.photomap.user.dto.UpdateUserRequest;
import ua.in.photomap.user.model.Provider;
import ua.in.photomap.user.model.Role;
import ua.in.photomap.user.model.User;
import ua.in.photomap.user.repository.UserRepository;
import ua.in.photomap.user.security.UserResolver;
import ua.in.photomap.user.service.EncryptionService;
import ua.in.photomap.user.service.RoleService;
import ua.in.photomap.user.service.UserService;
import ua.in.photomap.user.utils.Base64Utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final EncryptionService encryptionService;
    private final CacheManager cacheManager;

    @Override
    public Optional<User> findByEmailJoinPrivileges(String email) {
        return userRepository.findByEmailJoinPrivileges(email);
    }

    @Override
    public User createUnconfirmedUser(String username, String email, String password) {
        return create(username, email, Provider.LOCAL, Optional.of(password), false);
    }

    @Override
    public User createGoogleUser(String username, String email) {
        return create(username, email, Provider.GOOGLE, Optional.empty(), true);
    }

    @Override
    public User getById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found. Id: " + userId));
    }

    @Override
    @Transactional
    @CacheEvict(value = "isEnabledUser", key = "#userId")
    public void updateIsEnabled(Long userId, boolean isEnabled) {
        userRepository.updateIsEnabled(userId, isEnabled);
    }

    @Override
    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public void updateCurrentUserAccount(UpdateUserAccountRequest request) {
        Long userId = UserResolver.getCurrentUser().getId();
        userRepository.updateUserName(userId, request.getUsername());
    }

    @Override
    @Transactional
    public void updateCurrentUserPassword(String password) {
        Long userId = UserResolver.getCurrentUser().getId();
        updatePasswordByUserId(userId, password);
    }

    @Override
    public void updatePasswordByUserId(Long userId, String password) {
        userRepository.updatePassword(userId, encryptionService.encryptPassword(password));
    }

    @Override
    public Page<User> getAll(int page, int size) {
        return userRepository.findAllFetchRoles(PageRequest.of(page, size));
    }

    @Override
    @Transactional
    public User update(Long userId, UpdateUserRequest dto) {
        User user = getById(userId);
        if (DiffUtils.areDifferent(user.getUsername(), dto.getUsername())) {
            user.setUsername(dto.getUsername());
        }
        if (DiffUtils.areDifferent(user.getEmail(), dto.getEmail())) {
            user.setEmail(dto.getEmail());
        }
        List<Long> currentRolesIds = user.getRoles().stream()
                .map(Role::getId).toList();
        if (DiffUtils.areDifferent(currentRolesIds, dto.getRolesIds())) {
            user.setRoles(new HashSet<>(roleService.findAllById(dto.getRolesIds())));
            evictPrivilegesCache(user);
        }
        return userRepository.save(user);
    }

    @Override
    public GetUsersByIdsResponse getBasicInfo(List<Long> ids) {
        List<UserBasicDTO> users = userRepository.findAllBasicByIdIn(ids);
        return new GetUsersByIdsResponse(users);
    }

    private void evictPrivilegesCache(User user) {
        Cache userPrivilegesCache = cacheManager.getCache("getUserPrivileges");
        if (userPrivilegesCache != null) {
            userPrivilegesCache.evict(user.getId());
        }
    }

    private User getByEmailJoinPrivileges(String email) {
        return findByEmailJoinPrivileges(email)
                .orElseThrow(() -> new InternalException("User not found. Email: " + email));
    }

    private User create(String username, String email, Provider provider, Optional<String> passwordOptional,
                        Boolean isEnabled) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        passwordOptional.ifPresent(pass -> user.setPassword(encryptionService.encryptPassword(pass)));
        user.setProvider(provider);
        user.setIsEnabled(isEnabled);
        user.setIsNonLocked(true);
        user.setRoles(Collections.singleton(roleService.getUserRole()));
        userRepository.save(user);
        return getByEmailJoinPrivileges(email);
    }
}

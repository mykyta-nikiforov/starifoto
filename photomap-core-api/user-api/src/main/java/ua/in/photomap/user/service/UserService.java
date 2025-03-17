package ua.in.photomap.user.service;

import org.springframework.data.domain.Page;
import ua.in.photomap.common.photo.model.dto.batch.GetUsersByIdsResponse;
import ua.in.photomap.user.dto.UpdateUserAccountRequest;
import ua.in.photomap.user.dto.UpdateUserPasswordRequest;
import ua.in.photomap.user.dto.UpdateUserRequest;
import ua.in.photomap.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<User> findByEmailJoinPrivileges(String email);

    User createUnconfirmedUser(String username, String email, String password);

    User createGoogleUser(String username, String email);

    User getById(Long userId);

    void updateIsEnabled(Long userId, boolean isEnabled);

    Boolean existsByEmail(String email);

    void updateCurrentUserAccount(UpdateUserAccountRequest request);

    void updateCurrentUserPassword(String encodedPassword);

    Page<User> getAll(int page, int size);

    User update(Long userId, UpdateUserRequest dto);

    GetUsersByIdsResponse getBasicInfo(List<Long> ids);

    void updatePasswordByUserId(Long userId, String password);
}

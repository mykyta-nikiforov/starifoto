package ua.in.photomap.apigateway.service;

import java.util.Set;

public interface UserService {
    boolean isEnabledUser(Long userId);

    boolean isNonLockedUser(Long userId);

    Set<String> getUserPrivileges(Long userId);
}

package ua.in.photomap.apigateway.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ua.in.photomap.apigateway.service.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private static final String SQL_CHECK_USER_ENABLED = """
        SELECT COUNT(*) 
        FROM user_service.users 
        WHERE id = ? AND is_enabled = true""";
    
    private static final String SQL_CHECK_USER_NON_LOCKED = """
        SELECT COUNT(*) 
        FROM user_service.users 
        WHERE id = ? AND is_non_locked = true""";
    
    private static final String SQL_GET_USER_PRIVILEGES = """
        SELECT p.name 
        FROM user_service.users_roles ur 
        JOIN user_service.roles_privileges rp ON ur.role_id = rp.role_id 
        JOIN user_service.privilege p ON rp.privilege_id = p.id 
        WHERE ur.user_id = ?""";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean isEnabledUser(Long userId) {
        log.debug("Checking if user with id {} is enabled", userId);
        Integer count = jdbcTemplate.queryForObject(SQL_CHECK_USER_ENABLED, Integer.class, userId);
        boolean isEnabled = count != null && count > 0;
        log.debug("User with id {} is enabled: {}", userId, isEnabled);
        return isEnabled;
    }

    @Override
    public boolean isNonLockedUser(Long userId) {
        log.debug("Checking if user with id {} is non locked", userId);
        Integer count = jdbcTemplate.queryForObject(SQL_CHECK_USER_NON_LOCKED, Integer.class, userId);
        boolean isNonLocked = count != null && count > 0;
        log.debug("User with id {} is non locked: {}", userId, isNonLocked);
        return isNonLocked;
    }

    @Override
    public Set<String> getUserPrivileges(Long userId) {
        log.debug("Getting privileges for user with id {}", userId);
        List<String> privileges = jdbcTemplate.queryForList(SQL_GET_USER_PRIVILEGES, String.class, userId);
        log.debug("Privileges for user with id {}: {}", userId, privileges);
        return new HashSet<>(privileges);
    }
}
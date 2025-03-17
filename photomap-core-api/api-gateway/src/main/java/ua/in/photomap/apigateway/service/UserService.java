package ua.in.photomap.apigateway.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final JdbcTemplate jdbcTemplate;

    private static final String SQL_CHECK_USER_ENABLED = 
        "SELECT COUNT(*) FROM user_service.users WHERE id = ? AND is_enabled = true";
    
    private static final String SQL_CHECK_USER_NON_LOCKED = 
        "SELECT COUNT(*) FROM user_service.users WHERE id = ? AND is_non_locked = true";
    
    private static final String SQL_GET_USER_PRIVILEGES = 
        "SELECT p.name FROM user_service.users_roles ur " +
        "JOIN user_service.roles_privileges rp ON ur.role_id = rp.role_id " +
        "JOIN user_service.privilege p ON rp.privilege_id = p.id " +
        "WHERE ur.user_id = ?";

    public boolean isEnabledUser(Long userId) {
        log.debug("Checking if user with id {} is enabled", userId);
        Integer count = jdbcTemplate.queryForObject(SQL_CHECK_USER_ENABLED, Integer.class, userId);
        log.debug("User with id {} is enabled: {}", userId, count != null && count > 0);
        return count != null && count > 0;
    }

    public boolean isNonLockedUser(Long userId) {
        log.debug("Checking if user with id {} is non locked", userId);
        Integer count = jdbcTemplate.queryForObject(SQL_CHECK_USER_NON_LOCKED, Integer.class, userId);
        log.debug("User with id {} is non locked: {}", userId, count != null && count > 0);
        return count != null && count > 0;
    }

    public Set<String> getUserPrivileges(Long userId) {
        log.debug("Getting privileges for user with id {}", userId);
        List<String> result = jdbcTemplate.queryForList(SQL_GET_USER_PRIVILEGES, String.class, userId);
        log.debug("Privileges for user with id {}: {}", userId, result);
        return new HashSet<>(result);
    }
}
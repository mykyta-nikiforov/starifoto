package ua.`in`.photomap.apigateway.service.impl

import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import ua.`in`.photomap.apigateway.service.UserService

@Service
class UserServiceImpl(
    private val jdbcTemplate: JdbcTemplate
) : UserService {
    companion object {
        private val log = LoggerFactory.getLogger(UserServiceImpl::class.java)
        
        private const val SQL_CHECK_USER_ENABLED = """
            SELECT COUNT(*) 
            FROM user_service.users 
            WHERE id = ? AND is_enabled = true
        """
        
        private const val SQL_CHECK_USER_NON_LOCKED = """
            SELECT COUNT(*) 
            FROM user_service.users 
            WHERE id = ? AND is_non_locked = true
        """
        
        private const val SQL_GET_USER_PRIVILEGES = """
            SELECT p.name 
            FROM user_service.users_roles ur 
            JOIN user_service.roles_privileges rp ON ur.role_id = rp.role_id 
            JOIN user_service.privilege p ON rp.privilege_id = p.id 
            WHERE ur.user_id = ?
        """
    }

    override fun isEnabledUser(userId: Long): Boolean {
        log.debug("Checking if user with id {} is enabled", userId)
        val count = jdbcTemplate.queryForObject(SQL_CHECK_USER_ENABLED, Int::class.java, userId)
        val isEnabled = count != null && count > 0
        log.debug("User with id {} is enabled: {}", userId, isEnabled)
        return isEnabled
    }

    override fun isNonLockedUser(userId: Long): Boolean {
        log.debug("Checking if user with id {} is non locked", userId)
        val count = jdbcTemplate.queryForObject(SQL_CHECK_USER_NON_LOCKED, Int::class.java, userId)
        val isNonLocked = count != null && count > 0
        log.debug("User with id {} is non locked: {}", userId, isNonLocked)
        return isNonLocked
    }

    override fun getUserPrivileges(userId: Long): Set<String> {
        log.debug("Getting privileges for user with id {}", userId)
        val privileges = jdbcTemplate.queryForList(SQL_GET_USER_PRIVILEGES, String::class.java, userId)
        log.debug("Privileges for user with id {}: {}", userId, privileges)
        return privileges.toSet()
    }
} 
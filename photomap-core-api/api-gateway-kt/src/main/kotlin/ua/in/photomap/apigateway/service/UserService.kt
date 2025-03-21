package ua.`in`.photomap.apigateway.service

interface UserService {
    fun isEnabledUser(userId: Long): Boolean
    fun isNonLockedUser(userId: Long): Boolean
    fun getUserPrivileges(userId: Long): Set<String>
} 
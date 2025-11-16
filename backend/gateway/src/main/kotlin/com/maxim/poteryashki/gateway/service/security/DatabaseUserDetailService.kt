package com.maxim.poteryashki.gateway.service.security

import com.maxim.poteryashki.gateway.db.user.UserDao
import com.maxim.poteryashki.gateway.domain.User

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.User as SpringSecurityUser


class DatabaseUserDetailService(
    private val userDao: UserDao
): UserDetailsService {

    override fun loadUserByUsername(username: String?): UserDetails? =
        username
            ?.let { userDao.getByName(it) }
            ?.toUserDetails()


    private fun User.toUserDetails(): UserDetails {
        return SpringSecurityUser.withUsername(name)
            .password(password)
            .roles(userMetadata.role.name)
            .build()
    }
}
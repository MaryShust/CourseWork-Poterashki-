package com.maxim.poteryashki.gateway.service.security

import com.maxim.poteryashki.gateway.db.user.UserDao
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.UserDetailsManager
import org.springframework.stereotype.Component


class DatabaseUserDetailService(
    private val userDao: UserDao
): UserDetailsManager {

    //TODO реализовать по нормальному spring security
    override fun loadUserByUsername(username: String?): UserDetails? =
        username
            ?.let { userDao.getByName(it) }
            ?.toUserDetails()


    private fun com.maxim.poteryashki.gateway.domain.User.toUserDetails(): UserDetails {
        return User.withUsername(name)
            .password(password)
            .roles(role.name)
            .build()
    }
}
package com.maxim.poteryashki.auth.service

import com.maxim.poteryashki.auth.db.user.UserDao
import com.maxim.poteryashki.auth.domain.User
import org.springframework.stereotype.Service
import java.util.UUID

/**
 * Сервис для управления пользователями в БД
 */
@Service
class UserRegistry(
    private val userDao: UserDao,
) {

    /**
     * Создает нового пользователя
     * @return id пользователя
     */
    fun createUser(
        username: String,
        email: String,
        password: String,
    ): UUID {
        return userDao.create(User(
            id = null,
            name = username,
            email = email,
            password = password,
            metadata = null
        ))
    }

    /**
     * Получает пользователя по логину и паролю
     * @return id пользователя
     */
    fun getUser(
        email: String,
        password: String,
    ): UUID? {
        return userDao.getByEmail(email, password)?.id
    }

    fun update(
        user: User
    ) {
        return userDao.update(user)
    }

    fun getUserById(id: UUID): User? {
        return userDao.getById(id)
    }

}
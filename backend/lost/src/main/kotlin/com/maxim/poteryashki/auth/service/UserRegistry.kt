package com.maxim.poteryashki.auth.service

import com.maxim.poteryashki.auth.db.statistics.StatisticsDao
import com.maxim.poteryashki.auth.db.user.UserDao
import com.maxim.poteryashki.auth.domain.Statistics
import com.maxim.poteryashki.auth.domain.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

/**
 * Сервис для управления пользователями в БД
 */
@Service
class UserRegistry(
    private val userDao: UserDao,
    private val statisticsDao: StatisticsDao
) {

    /**
     * Создает нового пользователя
     * @return id пользователя
     */
    @Transactional
    fun createUser(
        username: String,
        email: String,
        password: String,
    ): UUID {
        val userId = userDao.create(User(
            id = null,
            name = username,
            email = email,
            password = password,
            metadata = null
        ))

        statisticsDao.save(Statistics(0, 0, 0, 0), userId)

        return userId
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
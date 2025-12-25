package com.maxim.poteryashki.auth.service

import com.maxim.poteryashki.auth.db.token.TokenDao
import com.maxim.poteryashki.auth.db.user.UserDao
import com.maxim.poteryashki.auth.domain.Token
import com.maxim.poteryashki.auth.domain.User
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.time.LocalDateTime
import java.util.Random
import java.util.UUID
import kotlin.random.asKotlinRandom

@Component
class TokenService(
    private val tokenDao: TokenDao,
    private val userDao: UserDao
) {

    private fun String.extractToken() = this.removePrefix("Bearer ").trim()

    fun getUserByHeader(header: String): User? {
        val token = tokenDao.getByToken(header.extractToken())

        if (token == null || token.isExpired()){
            return null
        }

        return userDao.getById(token.userId)
    }

    fun getUserById(userId: UUID) =
        userDao.getById(userId)

    fun generateToken(userId: UUID): String {
        val random = SecureRandom(userId.toString().toByteArray())
        val token = random.generateSecureRandomString()
        tokenDao.save(
            Token(
                content = token,
                userId = userId,
                expires = LocalDateTime.now().plusDays(1)
            )
        )
        return token
    }

    fun Random.generateSecureRandomString(length: Int = 10): String {
        val random = this.asKotlinRandom()
        val chars = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { chars.random(random) }
            .joinToString("")
    }

}
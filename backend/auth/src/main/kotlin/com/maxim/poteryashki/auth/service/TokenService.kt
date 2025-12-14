package com.maxim.poteryashki.auth.service

import com.maxim.poteryashki.auth.db.token.TokenDao
import com.maxim.poteryashki.auth.db.user.UserDao
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.util.Random
import java.util.UUID
import kotlin.random.asKotlinRandom

@Component
class TokenService(
    private val tokenDao: TokenDao,
    private val userDao: UserDao
) {

    fun getUserByToken(token: String) =
        tokenDao.getByToken(token)
            ?.takeIf { !it.isExpired() }
            ?.let { userDao.getById(it.userId) }

    fun generateToken(userId: UUID): String {
        val random = SecureRandom(userId.toString().toByteArray())
        return random.generateSecureRandomString()
    }

    fun Random.generateSecureRandomString(length: Int = 10): String {
        val random = this.asKotlinRandom()
        val chars = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { chars.random(random) }
            .joinToString("")
    }

}
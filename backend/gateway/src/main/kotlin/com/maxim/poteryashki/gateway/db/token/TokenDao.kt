package com.maxim.poteryashki.gateway.db.token

import com.maxim.poteryashki.gateway.domain.Token
import org.springframework.stereotype.Repository

@Repository
class TokenDao(
    private val tokenRepository: TokenRepository
) {

    fun getByToken(token: String): Token? =
        tokenRepository.findByToken(token)?.toDomain()

    fun save(token: Token) =
        tokenRepository.upsert(token.content, token.userId, token.expires)

}

private fun TokenDb.toDomain(): Token = Token(
    content = token,
    userId = userId,
    expires = expires
)
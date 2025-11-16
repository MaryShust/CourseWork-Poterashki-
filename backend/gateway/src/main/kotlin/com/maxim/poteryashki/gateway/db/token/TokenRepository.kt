package com.maxim.poteryashki.gateway.db.token

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface TokenRepository : CrudRepository<TokenDb, String> {

    @Query("SELECT * FROM token WHERE token = :token")
    fun findByToken(token: String): TokenDb?

    @Query("DELETE FROM token WHERE token = :token")
    fun deleteTokenDbByToken(token: String)

    @Query("UPDATE token SET expires = NOW() WHERE token = :token")
    fun expireToken(token: String)

    @Query(
        """INSERT INTO token (token, user_id, expires) 
            VALUES (:token, :userId, :expires) 
            ON CONFLICT (token) DO UPDATE SET expires = :expires"""
    )
    fun upsert(token: String, userId: UUID, expires: LocalDateTime)
}
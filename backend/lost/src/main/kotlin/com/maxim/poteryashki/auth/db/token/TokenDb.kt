package com.maxim.poteryashki.auth.db.token

import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.UUID

@Table
data class TokenDb(
    val token: String,
    val userId: UUID,
    val expires: LocalDateTime
)
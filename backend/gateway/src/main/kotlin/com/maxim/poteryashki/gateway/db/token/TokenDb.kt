package com.maxim.poteryashki.gateway.db.token

import java.time.LocalDateTime
import java.util.UUID

data class TokenDb(
    val token: String,
    val userId: UUID,
    val expires: LocalDateTime
)
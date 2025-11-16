package com.maxim.poteryashki.gateway.domain

import java.time.LocalDateTime
import java.util.UUID

data class Token(
    val content: String,
    val userId: UUID,
    val expires: LocalDateTime
) {
    fun isExpired(): Boolean {
        return expires.isBefore(LocalDateTime.now())
    }
}
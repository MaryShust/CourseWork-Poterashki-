package com.maxim.poteryashki.lost.db.found

import org.springframework.data.annotation.Id
import java.time.LocalDateTime
import java.util.UUID

data class FoundEntity(

    @Id
    val id: Long?,

    val owner: UUID,

    val description: String,

    val placeId: Long,

    val lostPeriodStart: LocalDateTime,

    val lostPeriodFinish: LocalDateTime,

    val createdAt: LocalDateTime,

    val found: Boolean,
)
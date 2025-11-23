package com.maxim.poteryashki.lost.db.completed

import java.time.LocalDateTime

data class CompletedEntity(

    val id: Long?,

    val lostId: Long,

    val foundId: Long,

    val completedAt: LocalDateTime,
)

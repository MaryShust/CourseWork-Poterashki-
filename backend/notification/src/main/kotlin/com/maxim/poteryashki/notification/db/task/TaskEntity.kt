package com.maxim.poteryashki.notification.db.task

import org.springframework.data.annotation.Id
import java.time.Instant

data class TaskEntity(

    @Id
    val id: Long?,

    val foundOwner: String,

    val lostOwner: String,

    val startTime: Instant,

    val status: String,
)
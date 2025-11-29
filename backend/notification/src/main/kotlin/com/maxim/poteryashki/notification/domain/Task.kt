package com.maxim.poteryashki.notification.domain

import java.time.Instant
import java.util.UUID

data class Task(
    val id: Long,
    val foundOwner: UUID,
    val lostOwner: UUID,
    val startTime: Instant,
    val status: TaskStatus
    )

enum class TaskStatus{
    CREATED,
    STARTED_CONVERSATION,
    WAITING_FOR_MEETUP,
    COMPLETED_EXCHANGE,
    FAILED_EXCHANGE,
}
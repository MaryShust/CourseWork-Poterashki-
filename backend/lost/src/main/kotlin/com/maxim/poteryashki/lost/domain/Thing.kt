package com.maxim.poteryashki.lost.domain

import java.time.Instant
import java.util.UUID

data class Thing(
    val id: String?,

    val title: String,

    val owner: UUID,

    val type: ThingType,

    val createdAt: Instant,

    /**
     * В зависимости от типа либо
     *
     * [com.maxim.poteryashki.lost.db.ThingType.LOST] - дата потери
     *
     * [com.maxim.poteryashki.lost.db.ThingType.FOUND] - дата обнаружения
     */
    val date: Instant,

    val place: Place,

    val description: String,

    val photos: List<String>,

    /**
     * id тех кто откликнулся
     */
    val responses: Set<UUID>?,

    /**
     * Сумма вознаграждения
     */
    val fee: Int?,

    val completedAt: Instant?,

    val version: Long? = null
)

enum class ThingType {
    /**
     * Поретерянная вещь
     */
    LOST,

    /**
     * Найденная вещь
     */
    FOUND,
}
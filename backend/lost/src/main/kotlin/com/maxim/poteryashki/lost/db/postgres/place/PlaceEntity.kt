package com.maxim.poteryashki.lost.db.postgres.place

import org.springframework.data.annotation.Id

data class PlaceEntity(

    @Id
    val id: Long?,

    val city: String,

    val street: String?,

    val house: String?,

    /**
     * Название места
     *
     * Поле для описания вещей оставленных в кафе
     */
    val placeName: String?,

    val extraDescription: String?,

)
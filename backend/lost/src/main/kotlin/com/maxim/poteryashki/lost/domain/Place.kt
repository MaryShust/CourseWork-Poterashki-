package com.maxim.poteryashki.lost.domain

data class Place(

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
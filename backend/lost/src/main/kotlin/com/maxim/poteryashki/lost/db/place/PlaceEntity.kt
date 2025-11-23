package com.maxim.poteryashki.lost.db.place

import org.springframework.data.annotation.Id

data class PlaceEntity(

    @Id
    val id: Long?,

    val city: String,

    val street: String?,

    val house: String?,

    val placeName: String?,

)
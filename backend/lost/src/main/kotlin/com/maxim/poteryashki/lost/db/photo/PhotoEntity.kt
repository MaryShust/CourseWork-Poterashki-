package com.maxim.poteryashki.lost.db.photo

import org.springframework.data.annotation.Id

data class PhotoEntity(

    @Id
    val id: Long?,

    val entity: String,

    val entityId: Long,

    val bucket: String,

    val key: String,

)

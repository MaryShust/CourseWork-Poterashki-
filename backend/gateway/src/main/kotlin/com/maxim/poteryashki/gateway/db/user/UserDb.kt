package com.maxim.poteryashki.gateway.db.user

import org.springframework.data.annotation.Id
import java.util.UUID

data class UserDb(

    @Id
    val id: UUID?,

    val name: String,

    val password: String,

    val role: String,

    val metadata: String

)
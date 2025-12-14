package com.maxim.poteryashki.auth.db.user

import org.springframework.data.annotation.Id
import java.util.UUID

data class UserDb(

    @Id
    val id: UUID?,

    val name: String,

    val email: String,

    val password: String,

    val metadata: String

)
package com.maxim.poteryashki.auth.db.user

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("users")
data class UserDb(

    @Id
    val id: UUID?,

    val name: String,

    val email: String,

    val password: String,

    val metadata: String

)
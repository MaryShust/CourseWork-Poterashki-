package com.maxim.poteryashki.auth.db.contact

import org.springframework.data.annotation.Id
import java.util.UUID

data class ContactDb(
    @Id
    val id: Long?,

    val userId: UUID,

    val data: String
)
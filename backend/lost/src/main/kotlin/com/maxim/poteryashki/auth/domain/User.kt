package com.maxim.poteryashki.auth.domain

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.time.Instant
import java.util.UUID

data class User(

    val id: UUID?,

    /**
     * Имя пользователя (ака логин)
     */
    val name: String,

    val email: String,

    val password: String,

    val metadata: UserMetadata?,

    val createdAt: Instant = Instant.now(),
)

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = BasicUserMetadata::class, name = "BasicMetadata")
)
sealed interface UserMetadata {
    /**
     * Реальное имя человека
     */
    val name: String
    val city: String
    val phone: String

    companion object {
        fun basic(
            name: String,
            city: String,
            phone: String
        ): UserMetadata = BasicUserMetadata(
            name = name,
            city = city,
            phone = phone
        )
    }
}

data class BasicUserMetadata(
    override val name: String,
    override val city: String,
    override val phone: String
) : UserMetadata
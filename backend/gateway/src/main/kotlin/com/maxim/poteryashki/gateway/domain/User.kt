package com.maxim.poteryashki.gateway.domain

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.time.LocalDateTime
import java.util.UUID

data class User(

    val id: UUID?,

    /**
     * Имя пользователя (ака логин)
     */
    val name: String,

    val password: String,

    val userMetadata: UserMetadata
)

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = BasicUserMetadata::class, name = "BasicMetadata")
)
sealed interface UserMetadata {
    val createdAt: LocalDateTime
    val isDeleted: Boolean
    val isBanned: Boolean
    val role: Role
}

data class BasicUserMetadata(
    /**
     * Реальное имя человека
     */
    private val name: String,
    private val surname: String,
    override val createdAt: LocalDateTime,
    override val isDeleted: Boolean,
    override val isBanned: Boolean, override val role: Role
) : UserMetadata

enum class Role {
    USER,
    ADMIN
}
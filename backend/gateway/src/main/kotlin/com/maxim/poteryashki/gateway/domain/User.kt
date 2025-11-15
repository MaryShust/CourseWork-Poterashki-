package com.maxim.poteryashki.gateway.domain

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.util.UUID

data class User(

    val id: UUID?,

    /**
     * Имя пользователя (ака логин)
     */
    val name: String,

    val password: String,

    val role: Role,

    val userMetadata: UserMetadata
)

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = BasicUserMetadata::class, name = "BasicMetadata")
)
sealed interface UserMetadata

data class BasicUserMetadata(
    /**
     * Реальное имя человека
     */
    private val name: String, private val surname: String
) : UserMetadata

enum class Role {
    USER,
    ADMIN
}
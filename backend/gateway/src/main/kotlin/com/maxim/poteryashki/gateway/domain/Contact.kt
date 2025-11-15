package com.maxim.poteryashki.gateway.domain

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.util.UUID

data class Contact(

    val userId: UUID,

    val data: ContactData
)


@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = TelegramContactData::class, name = "telegram")
)
sealed interface ContactData

data class TelegramContactData(
    private val userId: String
)
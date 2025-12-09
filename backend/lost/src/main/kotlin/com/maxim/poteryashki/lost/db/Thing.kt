package com.maxim.poteryashki.lost.db

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.DateFormat
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.time.Instant
import java.util.UUID


@Document(indexName = "entries")
data class Thing(
    @Id
    var id: String?,

    @Field(type = FieldType.Keyword)
    val owner: UUID,

    @Field(type = FieldType.Keyword)
    val type: ThingType,

    @Field(type = FieldType.Date, format = [DateFormat.date_time])
    val createdAt: Instant,

    @Field(type = FieldType.Nested)
    val place: Place,

    @Field(type = FieldType.Text)
    val description: String,

    @Field(type = FieldType.Keyword)
    val photos: List<String>,

    @Field(type = FieldType.Date, format = [DateFormat.date_time])
    val completedAt: Instant?,
)

data class Place(

    val city: String,

    val street: String?,

    val house: String?,

    /**
     * Название места
     *
     * Поле для описания вещей оставленных в кафе
     */
    val placeName: String?,

    val extraDescription: String?,
)

enum class ThingType {
    LOST,
    FOUND,
}
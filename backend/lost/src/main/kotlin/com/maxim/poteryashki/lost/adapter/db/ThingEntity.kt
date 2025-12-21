package com.maxim.poteryashki.lost.adapter.db

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.elasticsearch.annotations.DateFormat
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.time.Instant
import java.util.UUID


@Document(indexName = "entries")
data class ThingEntity(
    @Id
    var id: String?,

    @Field(type = FieldType.Text)
    val title: String,

    @Field(type = FieldType.Keyword)
    val owner: String,

    @Field(type = FieldType.Keyword)
    val type: ThingType,

    @Field(type = FieldType.Date, format = [DateFormat.date_time])
    val createdAt: Instant,

    /**
     * В зависимости от типа либо
     *
     * [com.maxim.poteryashki.lost.db.ThingType.LOST] - дата потери
     *
     * [com.maxim.poteryashki.lost.db.ThingType.FOUND] - дата обнаружения
     */
    @Field(type = FieldType.Date, format = [DateFormat.date_time])
    val date: Instant,

    @Field(type = FieldType.Nested)
    val place: Place,

    @Field(type = FieldType.Text)
    val description: String,

    @Field(type = FieldType.Integer)
    val fee: Int?,

    @Field(type = FieldType.Keyword)
    val photos: List<String>,

    @Field(type = FieldType.Keyword)
    val responses: List<UUID>?,

    @Field(type = FieldType.Date, format = [DateFormat.date_time])
    val completedAt: Instant?,

    /**
     * ElasticSearch не поддерживает ACID и транзакции
     *
     * Версионирование - единсвенный способ обеспечить +- согласованность
     */
    @Version
    var version: Long? = null
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
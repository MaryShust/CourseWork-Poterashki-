package com.maxim.poteryashki.lost.db.elastic

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document

@Document(indexName = "found_entities")
data class FoundElasticEntity(

    @Id
    val id: Long,

    val description: String,

    val city: String,

    val street: String?,

    val house: String?,

    val placeName: String?,

    val extraDescription: String?,
)
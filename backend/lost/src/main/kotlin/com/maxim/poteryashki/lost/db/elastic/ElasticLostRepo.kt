package com.maxim.poteryashki.lost.db.elastic

import co.elastic.clients.elasticsearch._types.query_dsl.Query
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders
import org.springframework.data.elasticsearch.client.elc.NativeQuery
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHit
import org.springframework.stereotype.Repository

data class FuzzyFieldConfig(
    val fuzziness: String = "AUTO",   // "0", "1", "2", "AUTO"
    val boost: Float = 1.0f,
    val prefixLength: Int = 1,       // Сколько первых символов без ошибок
)

private val lost = object {
    val description = FuzzyFieldConfig(fuzziness = "3", boost = 1.7f)

    val city = FuzzyFieldConfig(fuzziness = "0")

    val street = FuzzyFieldConfig(fuzziness = "2", boost = 1.5f)

    val house = FuzzyFieldConfig()

    val placeName = FuzzyFieldConfig(fuzziness = "3", boost = 1.5f)

    val extraDescription = FuzzyFieldConfig(boost = 1.7f)

}

private val found = object {
    val description = FuzzyFieldConfig(fuzziness = "3", boost = 1.7f)

    val city = FuzzyFieldConfig(fuzziness = "0")

    val street = FuzzyFieldConfig(fuzziness = "2", boost = 1.5f)

    val house = FuzzyFieldConfig()

    val placeName = FuzzyFieldConfig(fuzziness = "3", boost = 1.5f)

    val extraDescription = FuzzyFieldConfig(boost = 1.7f)

}

@Repository
class ElasticEntityRepo(
    private val elasticOperations: ElasticsearchOperations,
) {

    fun findLostByDescription(
        description: String,
        city: String,
        street: String?,
        house: String?,
        placeName: String?,
        extraDescription: String?,
    ): List<LostElasticEntity> {

        val mustQueries = mutableListOf<Query>()

        // Обязательные поля
        if (description.isNotBlank()) {
            mustQueries += matchQuery("description", description, lost.description)
        }
        if (city.isNotBlank()) {
            mustQueries += matchQuery("city", city, lost.city)
        }

        // Необязательные поля — добавляем только если не пустые
        street?.takeIf { it.isNotBlank() }?.let {
            mustQueries += matchQuery("street", it, lost.street)
        }
        house?.takeIf { it.isNotBlank() }?.let {
            mustQueries += matchQuery("house", it, lost.house)
        }
        placeName?.takeIf { it.isNotBlank() }?.let {
            mustQueries += matchQuery("placeName", it, lost.placeName)
        }
        extraDescription?.takeIf { it.isNotBlank() }?.let {
            mustQueries += matchQuery("extraDescription", it, lost.extraDescription)
        }

        val boolQuery = QueryBuilders.bool { b ->
            b.must(mustQueries)
        }

        val nativeQuery = NativeQuery.builder()
            .withQuery(boolQuery)
            .build()

        val searchHits = elasticOperations.search(nativeQuery, LostElasticEntity::class.java)
        return searchHits.searchHits.map { it.content }
    }

    fun findFoundByDescription(
        description: String,
        city: String,
        street: String?,
        house: String?,
        placeName: String?,
        extraDescription: String?,
    ): List<FoundElasticEntity> {

        val mustQueries = mutableListOf<Query>()

        // Обязательные поля
        if (description.isNotBlank()) {
            mustQueries += matchQuery("description", description, found.description)
        }
        if (city.isNotBlank()) {
            mustQueries += matchQuery("city", city, found.city)
        }

        // Необязательные поля — добавляем только если не пустые
        street?.takeIf { it.isNotBlank() }?.let {
            mustQueries += matchQuery("street", it, found.street)
        }
        house?.takeIf { it.isNotBlank() }?.let {
            mustQueries += matchQuery("house", it, found.house)
        }
        placeName?.takeIf { it.isNotBlank() }?.let {
            mustQueries += matchQuery("placeName", it, found.placeName)
        }
        extraDescription?.takeIf { it.isNotBlank() }?.let {
            mustQueries += matchQuery("extraDescription", it, found.extraDescription)
        }

        val boolQuery = QueryBuilders.bool { b ->
            b.must(mustQueries)
        }

        val nativeQuery = NativeQuery.builder()
            .withQuery(boolQuery)
            .build()

        val searchHits = elasticOperations.search(nativeQuery, FoundElasticEntity::class.java)
        return searchHits.searchHits.map { it.content }
    }

    private fun matchQuery(field: String, value: String, cfg: FuzzyFieldConfig): Query {
        return QueryBuilders.match { m ->
            m.field(field)
                .query(value)
                .fuzziness(cfg.fuzziness)       // "0", "1", "2", "AUTO" — как в конфиге
                .prefixLength(cfg.prefixLength) // фиксируем префикс без ошибок
                .boost(cfg.boost)               // вес поля
        }
    }
}


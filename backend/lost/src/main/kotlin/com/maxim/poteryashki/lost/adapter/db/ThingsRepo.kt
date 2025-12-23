package com.maxim.poteryashki.lost.adapter.db

import co.elastic.clients.elasticsearch._types.FieldValue
import co.elastic.clients.elasticsearch._types.query_dsl.Operator
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import co.elastic.clients.elasticsearch._types.query_dsl.Query as EsQuery
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders as Q
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.elasticsearch.client.elc.NativeQuery
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.query.Criteria
import org.springframework.data.elasticsearch.core.query.CriteriaQuery
import org.springframework.stereotype.Repository
import java.time.Duration
import java.time.Instant
import java.util.UUID

data class ThingFilter(
    val type: ThingType? = null,
    val date: Instant? = null,
    val place: Place? = null,
    val description: String? = null,
    val completed: Boolean? = null
)

interface ThingRepository {
    fun findAllBy(filter: ThingFilter, pageable: Pageable): Pair<List<ThingEntity>, Long>

    fun findAllByOwner(owner: UUID, pageable: Pageable): Pair<List<ThingEntity>, Long>

    fun create(thingEntity: ThingEntity): ThingEntity

    fun update(thingEntity: ThingEntity): ThingEntity

    fun getById(id: String): ThingEntity?

    fun existsById(id: String): Boolean

}

@Repository
class ThingRepositoryImpl(
    private val operations: ElasticsearchOperations
): ThingRepository {

    /**
     * Поиск вещей по всем указаным параметрам
     */
    override fun findAllBy(filter: ThingFilter, pageable: Pageable): Pair<List<ThingEntity>, Long> {

        val typeQuery = queryIfNotNull(filter.type) { type ->
            Q.term {
                it.field("type.keyword").value(FieldValue.of(type.name))
            }
        }

        val dateQuery = dateQuery(filter.date)

        val completedQuery = completedQuery(filter.completed)

        val descriptionQuery = queryIfNotNull(filter.description) { text ->
            Q.match {
                it.field("description")
                    .query(text)
                    .operator(Operator.And)
            }
        }

        val placeQuery = placeQuery(filter.place)

        val filterQuery = listOfNotNull(
            typeQuery,
            completedQuery,
        )

        val mustQuery = listOfNotNull(
            dateQuery,
            descriptionQuery,
        ) + placeQuery

        val esQuery = buildQuery(filterQuery, mustQuery)


        val query = NativeQuery.builder()
            .withQuery(esQuery)
            .withPageable(pageable)
            .build()

        val hits = operations.search(query, ThingEntity::class.java)
        val total = hits.totalHits

        return hits.map { it.content }.toList() to total
    }

    override fun findAllByOwner(owner: UUID, pageable: Pageable): Pair<List<ThingEntity>, Long> {
        val criteria = Criteria("owner").`is`(owner)
        val query = CriteriaQuery(criteria)

        val hits = operations.search(query, ThingEntity::class.java)
        val total = hits.totalHits

        return hits.map { it.content }.toList() to total
    }

    override fun getById(id: String): ThingEntity? {
        val result = operations.get<ThingEntity>(id, ThingEntity::class.java)
        return result
    }

    override fun create(thingEntity: ThingEntity): ThingEntity {
        val toSave = thingEntity.copy(id = null)
        return operations.save(toSave)
    }

    override fun update(thingEntity: ThingEntity): ThingEntity {
        return operations.save(thingEntity)
    }

    override fun existsById(id: String): Boolean {
        return getById(id) != null
    }

    private fun <T> queryIfNotNull(value: T?, query: (T) -> EsQuery): EsQuery? =
        value?.let { query(it) }

    private fun completedQuery(completed: Boolean?): EsQuery? =
        queryIfNotNull(completed) { value ->
            if (value) {
                Q.exists {
                    it.field("completedAt")
                }
            } else
                Q.bool { query ->
                    query.mustNot(Q.exists {
                        it.field("completedAt")
                    })
                }
        }

    private fun dateQuery(date: Instant?): EsQuery? =
        queryIfNotNull(date) { date ->
            val from = date.minus(Duration.ofDays(4))
            val to = date.plus(Duration.ofDays(4))
            Q.range { range ->
                range.date {
                    it.field("date")
                        .gte(from.toString())
                        .lte(to.toString())
                }
            }
        }

    private fun placeQuery(p: Place?): List<EsQuery> {
        if (p == null) {
            return emptyList()
        }

        val placeMust = mutableListOf<EsQuery>()
        placeMust.fuzzy("city", p.city)
        placeMust.fuzzy("street", p.street)
        placeMust.fuzzy("house", p.house)
        placeMust.fuzzy("placeName", p.placeName)

        // extraDescription
        val extraDescriptionQuery = queryIfNotNull(p.extraDescription) { text ->
            Q.match {
                it.field("extraDescription")
                    .query(text)
                    .operator(Operator.And)
            }
        }

        if (extraDescriptionQuery != null) {
            placeMust += extraDescriptionQuery
        }


        return placeMust
    }

    private fun MutableList<EsQuery>.fuzzy(field: String, value: String?) {
        if (!value.isNullOrBlank()) {
            this += Q.match { m ->
                m.field("place.$field")
                    .query(value)
                    .fuzziness("AUTO")
            }
        }
    }

    private fun buildQuery(filters: List<EsQuery>, must: List<EsQuery>): EsQuery {
        if (filters.isEmpty() && must.isEmpty()) {
            return Q.matchAll { it }
        }

        return Q.bool {
            var query = it
            if (filters.isNotEmpty()) {
                query = query.filter(filters)
            }
            if (must.isNotEmpty()) {
                query = query.must(must)
            }
            return@bool query
        }
    }

}
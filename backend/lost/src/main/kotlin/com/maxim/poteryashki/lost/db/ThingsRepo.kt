package com.maxim.poteryashki.lost.db

import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders
import co.elastic.clients.elasticsearch.core.search.ScoreMode
import org.springframework.data.elasticsearch.client.elc.NativeQuery
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.UUID

import co.elastic.clients.elasticsearch._types.FieldValue
import co.elastic.clients.elasticsearch._types.query_dsl.ChildScoreMode
import co.elastic.clients.elasticsearch._types.query_dsl.Operator
import org.springframework.data.domain.Pageable
import co.elastic.clients.elasticsearch._types.query_dsl.Query as EsQuery
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders as Q

data class ThingFilter(
    val owner: UUID? = null,
    val type: ThingType? = null,
    val createdAt: Instant? = null,
    val place: Place? = null,          // fuzziness для всех полей, если они не пустые
    val description: String? = null,
    val photos: List<String>? = null,  // если указан — требуем наличие каждого элемента
    val completedAt: Instant? = null
)

@Repository
class ThingFindRepositoryImpl(
    private val operations: ElasticsearchOperations
)  {

    fun findAllBy(filter: ThingFilter, pageable: Pageable): List<Thing> {
        val must = mutableListOf<EsQuery>()
        val filterQs = mutableListOf<EsQuery>()
        val mustNot = mutableListOf<EsQuery>()

        // type (Keyword)
        filter.type?.let { type ->
            filterQs += Q.term { t ->
                t.field("type").value(FieldValue.of(type.name))
            }
        }


        // description (Text) — полнотекстовый поиск
        filter.description?.let { text ->
            must += Q.match { m ->
                m.field("description")
                    .query(text)
                    .operator(Operator.And)
            }
        }

        // place (Nested) — fuzziness для каждого непустого поля
        filter.place?.let { p ->
            val placeMust = mutableListOf<EsQuery>()

            placeMust.fuzzy("city", p.city)
            placeMust.fuzzy("street", p.street)
            placeMust.fuzzy("house", p.house)
            placeMust.fuzzy("placeName", p.placeName)
            placeMust.fuzzy("extraDescription", p.extraDescription)

            if (placeMust.isNotEmpty()) {
                must += EsQuery.of { q ->
                    q.nested { n ->
                        n.path("place")
                            .query(
                                EsQuery.of { qq ->
                                    qq.bool { bb ->
                                        bb.must(placeMust)
                                    }
                                }
                            )
                            .scoreMode(ChildScoreMode.Avg)
                    }
                }
            }
        }

        val esBool = EsQuery.of { q ->
            q.bool { b ->
                if (filterQs.isNotEmpty()) b.filter(filterQs)
                if (must.isNotEmpty()) b.must(must)
                if (mustNot.isNotEmpty()) b.mustNot(mustNot)
                else b
            }
        }

        val query = NativeQuery.builder()
            .withQuery(esBool)
            .withPageable(pageable)
            .build()

        val hits = operations.search(query, Thing::class.java)
        return hits.map { it.content }.toList()
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
}
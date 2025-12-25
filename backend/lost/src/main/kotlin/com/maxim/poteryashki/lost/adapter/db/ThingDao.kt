package com.maxim.poteryashki.lost.adapter.db

import com.maxim.poteryashki.lost.domain.Place
import com.maxim.poteryashki.lost.domain.Thing
import com.maxim.poteryashki.lost.domain.ThingType
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.UUID

@Component
class ThingDao(
    private val thingRepository: ThingRepository
) {

    fun findAllBy(
        title: String?,
        type: ThingType?,
        date: Instant?,
        place: Place,
        description: String?,
        completed: Boolean?,
        pageable: Pageable,
        owner: UUID,
    ): Page {
        val (hints, total) = thingRepository.findAllBy(
            ThingFilter(
                title = title,
                type = type?.toEntity(),
                date = date,
                place = place.toEntity(),
                description = description,
                completed = completed
            ), owner, pageable
        )

        return Page(hints.map { it.toDomain() }, total)
    }

    fun findAllByOwner(owner: UUID, pageable: Pageable): Page {
        val (hints, total) = thingRepository.findAllByOwner(owner, pageable)

        return Page(hints.map { it.toDomain() }, total)
    }

    fun create(thing: Thing) =
        thingRepository.create(thing.toEntity())
            .toDomain()

    fun update(thing: Thing) = thingRepository.update(thing.toEntity()).toDomain()

    fun getById(id: String): Thing? = thingRepository.getById(id)?.toDomain()

    fun existsById(id: String): Boolean = thingRepository.existsById(id)

}

data class Page(
    val hints: List<Thing>,
    val total: Long
)
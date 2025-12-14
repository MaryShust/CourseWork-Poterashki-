package com.maxim.poteryashki.lost.adapter.db

import com.maxim.poteryashki.lost.domain.Place
import com.maxim.poteryashki.lost.domain.Thing
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.UUID

@Component
class ThingDao(
    private val thingRepository: ThingRepository
) {

    fun findAllBy(
        type: ThingType?,
        date: Instant?,
        place: Place,
        description: String?,
        pageable: Pageable
    ): List<Thing> =
        thingRepository.findAllBy(
            ThingFilter(
                type = type,
                date = date,
                place = place.toEntity(),
                description = description,
            ), pageable
        )
            .map { it.toDomain() }


    fun findAllByOwner(owner: UUID, pageable: Pageable): List<Thing> =
        thingRepository.findAllByOwner(owner, pageable)
            .map { it.toDomain() }

    fun create(thing: Thing) =
        thingRepository.create(thing.toEntity())
            .toDomain()

    fun update(thing: Thing) = thingRepository.update(thing.toEntity()).toDomain()

    fun getById(id: String): Thing? = thingRepository.getById(id)?.toDomain()

    fun existsById(id: String): Boolean = thingRepository.existsById(id)


}
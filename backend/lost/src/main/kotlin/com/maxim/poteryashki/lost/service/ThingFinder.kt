package com.maxim.poteryashki.lost.service

import com.maxim.poteryashki.lost.adapter.db.ThingDao
import com.maxim.poteryashki.lost.adapter.db.ThingFilter
import com.maxim.poteryashki.lost.adapter.db.ThingRepository
import com.maxim.poteryashki.lost.adapter.db.ThingType
import com.maxim.poteryashki.lost.domain.Place
import com.maxim.poteryashki.lost.domain.Thing
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

/**
 * Сервис который отвечает за поиск вещей
 */
@Service
class ThingFinder(
    private val thingDao: ThingDao,
) {

    fun find(
        type: ThingType?,
        date: Instant?,
        place: Place,
        description: String?,
        pageable: Pageable,
    ): List<Thing> =
        thingDao.findAllBy(
            type = type,
            date = date,
            place = place,
            description = description,
            pageable = pageable
        )

    fun findByOwner(owner: UUID, pageable: Pageable): List<Thing> =
        thingDao.findAllByOwner(owner, pageable)

    fun findById(id: String): Thing? =
        thingDao.getById(id)

    fun existsById(id: String): Boolean =
        thingDao.existsById(id)
}
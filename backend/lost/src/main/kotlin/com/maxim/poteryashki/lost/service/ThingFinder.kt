package com.maxim.poteryashki.lost.service

import com.maxim.poteryashki.lost.adapter.db.Page
import com.maxim.poteryashki.lost.adapter.db.ThingDao
import com.maxim.poteryashki.lost.domain.Place
import com.maxim.poteryashki.lost.domain.Thing
import com.maxim.poteryashki.lost.domain.ThingType
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
        userId: UUID, // Тот от кого пришел запрос
        type: ThingType?,
        date: Instant?,
        place: Place,
        description: String?,
        pageable: Pageable,
    ): Page {
        val page = thingDao.findAllBy(
            type = type,
            date = date,
            place = place,
            description = description,
            pageable = pageable
        )

        return page.copy(
            hints = page.hints.map { hideResponses(it, userId) }
        )
    }

    fun findByOwner(owner: UUID, pageable: Pageable): Page{
        val page = thingDao.findAllByOwner(owner, pageable)

        return page.copy(
            hints = page.hints.map { hideResponses(it, owner) }
        )
    }

    fun findById(id: String, user: UUID): Thing? =
        thingDao.getById(id)
            ?.let { hideResponses(it, user) }

    fun existsById(id: String): Boolean =
        thingDao.existsById(id)

    private fun hideResponses(thing: Thing, user: UUID) =
        if (thing.owner != user) {
            thing.copy(responses = null)
        } else if (thing.completedAt != null){
            thing.copy(responses = null)
        } else {
            thing
        }
}
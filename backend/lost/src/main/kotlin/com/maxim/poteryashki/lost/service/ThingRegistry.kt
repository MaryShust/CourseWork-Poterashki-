package com.maxim.poteryashki.lost.service

import com.maxim.poteryashki.auth.service.StatisticsService
import com.maxim.poteryashki.lost.adapter.db.ThingDao
import com.maxim.poteryashki.lost.adapter.s3.S3Adapter
import com.maxim.poteryashki.lost.domain.Place
import com.maxim.poteryashki.lost.domain.Thing
import com.maxim.poteryashki.lost.domain.ThingType
import com.maxim.poteryashki.lost.domain.exception.ThingNotFoundException
import com.maxim.poteryashki.lost.domain.exception.ThingVersionMismatchException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

private val logger = LoggerFactory.getLogger(ThingRegistry::class.java)

/**
 * Сервис управляет состоянием [Thing] в базе данных + S3
 * Старается обеспечить согласованность данных через версионирование
 */
@Service
class ThingRegistry(
    private val thingDao: ThingDao,
    private val s3Adapter: S3Adapter,
    private val statisticsService: StatisticsService
) {

    fun create(
        type: ThingType,
        date: Instant,
        place: Place,
        description: String,
        owner: UUID
    ): Thing {

        val toSave = Thing(
            id = null,
            owner = owner,
            type = type,
            createdAt = Instant.now(),
            date = date,
            place = place,
            description = description,
            photos = emptyList(),
            completedAt = null,
        )

        statisticsService.incrementActive(owner)

        return thingDao.create(toSave)
    }

    fun update(thing: Thing, actor: UUID) {
        val existing = thingDao.getById(thing.id!!)

        if (existing == null) {
            throw ThingNotFoundException(thing.id)
        }
        if (existing.owner != actor) {
            throw ThingNotFoundException(thing.id)
        }
        if (existing.version != thing.version) {
            throw ThingVersionMismatchException(thing.version, existing.version)
        }

        if (existing.completedAt == null && thing.completedAt != null && thing.type == ThingType.LOST) {
            statisticsService.incrementFound(actor)
            statisticsService.decrementActive(actor)
        }
        thingDao.update(thing)

    }


    fun addImage(
        thingId: String,
        thingVersion: Long?,
        image: ByteArray,
        fileEncoding: String
    ): Thing {
        val existing = thingDao.getById(thingId)

        if (existing == null) {
            throw ThingNotFoundException(thingId)
        }

        if (existing.version != thingVersion) {
            throw ThingVersionMismatchException(thingVersion, existing.version)
        }

        try {
            val imageName = "${existing.id}/${existing.photos.size + 1}.$fileEncoding"
            val link = s3Adapter.uploadPhoto(image, imageName)
            val updated = existing.copy(
                photos = existing.photos + link
            )
            return thingDao.update(updated)
        } catch (e: Exception) {
            logger.error("Failed to upload image", e)
            throw RuntimeException("Failed to upload image", e)
        }
    }

}
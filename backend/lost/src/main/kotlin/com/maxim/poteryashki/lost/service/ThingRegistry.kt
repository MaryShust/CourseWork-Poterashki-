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
    private val statisticsService: StatisticsService,
    private val thingMergeService: ThingMergeService
) {

    fun create(
        title: String,
        fee: Int?,
        type: ThingType,
        date: Instant,
        place: Place,
        description: String,
        owner: UUID
    ): Thing {

        val toSave = Thing(
            id = null,
            title = title,
            fee = fee,
            owner = owner,
            type = type,
            createdAt = Instant.now(),
            date = date,
            place = place,
            description = description,
            photos = emptyList(),
            responses = null,
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

        val merged = thingMergeService.mergeResponses(existing, thing)

        if (existing.completedAt == null && thing.completedAt != null && thing.type == ThingType.LOST) {
            statisticsService.incrementFound(actor)
            statisticsService.decrementActive(actor)
            statisticsService.addFee(thing.fee ?: 0, actor)
        }

        thingDao.update(merged.incrementVersion())
    }

    fun complete(thingId: String, actor: UUID) {
        val existing = thingDao.getById(thingId)

        if (existing == null) {
            throw ThingNotFoundException(thingId)
        }
        if (existing.owner != actor) {
            throw ThingNotFoundException(thingId)
        }

        val updated = existing.copy(completedAt = Instant.now()).incrementVersion()

        thingDao.update(updated)

        statisticsService.incrementFound(actor)
        statisticsService.decrementActive(actor)
        statisticsService.addFee(updated.fee ?: 0, actor)
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

        try {
            val imageName = "${existing.id}/${existing.photos.size + 1}.$fileEncoding"
            val link = s3Adapter.uploadPhoto(image, imageName)
            val updated = existing.copy(
                photos = existing.photos + link
            )
            val afterMerge = thingMergeService.mergeResponses(existing, updated)
            return thingDao.update(afterMerge.incrementVersion())
        } catch (e: Exception) {
            logger.error("Failed to upload image", e)
            throw RuntimeException("Failed to upload image", e)
        }
    }

    fun addResponse(
        thingId: String,
        userId: UUID
    ) {
        val existing = thingDao.getById(thingId)

        if (existing == null) {
            throw ThingNotFoundException(thingId)
        }

        val existingResponses = existing.responses ?: mutableListOf()

        val updated = existingResponses + userId

        thingDao.update(existing.copy(responses = updated).incrementVersion())
    }

    private fun Thing.incrementVersion() = this.copy(version = (version ?: 0) +1)

}
package com.maxim.poteryashki.lost.controller

import com.maxim.poteryashki.lost.api.DefaultApiDelegate
import com.maxim.poteryashki.lost.domain.Thing
import com.maxim.poteryashki.lost.domain.exception.ForbiddenModification
import com.maxim.poteryashki.lost.domain.exception.ThingNotFoundException
import com.maxim.poteryashki.lost.domain.exception.ThingVersionMismatchException
import com.maxim.poteryashki.lost.dto.ErrorResponse
import com.maxim.poteryashki.lost.dto.ThingDto
import com.maxim.poteryashki.lost.dto.ThingCreateDto
import com.maxim.poteryashki.lost.dto.ThingGetDto
import com.maxim.poteryashki.lost.service.ThingFinder
import com.maxim.poteryashki.lost.service.ThingRegistry
import org.springframework.core.io.Resource
import org.springframework.data.domain.Pageable
import org.springframework.util.StringUtils
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.ExceptionHandler
import java.util.UUID

@Component
class DefaultControllerDelegateImpl(
    private val thingRegistry: ThingRegistry,
    private val thingFinder: ThingFinder,
) : DefaultApiDelegate {
    override fun createThing(
        authorization: String,
        thingCreateDto: ThingCreateDto
    ): ResponseEntity<ThingDto> {
        //TODO add owner


        val created = thingRegistry.create(
            type = thingCreateDto.type.toDomain(),
            date = thingCreateDto.date.toInstant(),
            description = thingCreateDto.description,
            place = thingCreateDto.place.toDomain(),
            owner = UUID.randomUUID()
        )

        return ResponseEntity.ok(created.toDto())
    }

    override fun getThingById(
        id: String,
        authorization: String
    ): ResponseEntity<ThingDto> {
        val thing = thingFinder.findById(id)
        return thing?.toDto()?.let { ResponseEntity.ok(it) }
            ?: throw ThingNotFoundException(id)
    }

    override fun getThingByOwner(
        authorization: String,
        page: Int,
        size: Int,
        sort: List<String>?
    ): ResponseEntity<List<ThingDto>> {
        val response = thingFinder.findByOwner(
            owner = UUID.randomUUID(),
            pageable = createPageable(page, size, sort)
        ).map { it.toDto() }

        return ResponseEntity.ok(response)
    }


    override fun getThings(
        authorization: String,
        thingGetDto: ThingGetDto,
        page: Int,
        size: Int,
        sort: List<String>?
    ): ResponseEntity<List<ThingDto>> {
        val pageable = createPageable(page, size, sort)
        val response = thingFinder.find(
            type = thingGetDto.type?.toDomain(),
            date = thingGetDto.date?.toInstant(),
            place = thingGetDto.place.toDomain(),
            description = thingGetDto.description,
            pageable = pageable
        )

        return ResponseEntity.ok(response.map { it.toDto() })
    }

    override fun updateThingStatus(
        id: String,
        authorization: String,
        thingGetDto: ThingGetDto
    ): ResponseEntity<Unit> {
        TODO("Not yet implemented")
        // Подумать нужен ли он
    }

    override fun uploadPhoto(id: String, authorization: String, version: Long, file: Resource?): ResponseEntity<Unit> {
        if (file == null) {
            throw IllegalArgumentException("File is null")
        }

        val fileEncoding = StringUtils.getFilenameExtension(file.filename)

        if (fileEncoding == null) {
            throw IllegalArgumentException("File encoding is null")
        }

        thingRegistry.addImage(id, version, file.contentAsByteArray, fileEncoding)
        return ResponseEntity.ok().build()
    }


    @ExceptionHandler(ForbiddenModification::class)
    fun handleForbiddenModification(e: ForbiddenModification): ResponseEntity<ErrorResponse?> {
        return ResponseEntity.status(403).body(e.toResponse())
    }

    @ExceptionHandler(ThingNotFoundException::class)
    fun handleThingNotFoundException(e: ThingNotFoundException): ResponseEntity<Unit> {
        return ResponseEntity.notFound().build()
    }

    @ExceptionHandler(ThingVersionMismatchException::class)
    fun handleThingVersionMismatchException(e: ThingVersionMismatchException): ResponseEntity<ErrorResponse?> {
        return ResponseEntity.status(409).body(e.toResponse())
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(e: IllegalArgumentException): ResponseEntity<ErrorResponse?> {
        return ResponseEntity.badRequest().body(e.toResponse())
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ErrorResponse?> {
        return ResponseEntity.internalServerError().body(e.toResponse())
    }
}
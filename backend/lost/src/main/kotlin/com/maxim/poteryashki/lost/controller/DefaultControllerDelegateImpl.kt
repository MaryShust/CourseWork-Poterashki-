package com.maxim.poteryashki.lost.controller

import com.maxim.poteryashki.auth.service.TokenService
import com.maxim.poteryashki.lost.api.DefaultApiDelegate
import com.maxim.poteryashki.lost.domain.Thing
import com.maxim.poteryashki.lost.domain.exception.ForbiddenModification
import com.maxim.poteryashki.lost.domain.exception.ThingNotFoundException
import com.maxim.poteryashki.lost.domain.exception.ThingVersionMismatchException
import com.maxim.poteryashki.lost.dto.ErrorResponse
import com.maxim.poteryashki.lost.dto.GetThings200Response
import com.maxim.poteryashki.lost.dto.ThingDto
import com.maxim.poteryashki.lost.dto.ThingCreateDto
import com.maxim.poteryashki.lost.dto.ThingGetDto
import com.maxim.poteryashki.lost.service.ThingFinder
import com.maxim.poteryashki.lost.service.ThingRegistry
import org.slf4j.LoggerFactory
import org.springframework.core.io.Resource
import org.springframework.http.HttpStatus
import org.springframework.util.StringUtils
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.ExceptionHandler
import java.util.UUID

private val logger = LoggerFactory.getLogger(DefaultControllerDelegateImpl::class.java)

@Component
class DefaultControllerDelegateImpl(
    private val thingRegistry: ThingRegistry,
    private val thingFinder: ThingFinder,
    private val tokenService: TokenService,
) : DefaultApiDelegate {
    override fun completeThing(
        id: String,
        authorization: String
    ): ResponseEntity<Unit> {
        val user = tokenService.getUserByHeader(authorization)

        if (user == null) {
            logger.info("User not found for token: $authorization")
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        thingRegistry.complete(id, user.id!!)

        return ResponseEntity.ok().build()
    }

    override fun createThing(
        authorization: String,
        thingCreateDto: ThingCreateDto
    ): ResponseEntity<ThingDto> {
        val user = tokenService.getUserByHeader(authorization)

        if (user == null) {
            logger.info("User not found for token: $authorization")
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        val created = thingRegistry.create(
            title = thingCreateDto.title,
            fee = thingCreateDto.fee,
            type = thingCreateDto.type.toDomain(),
            date = thingCreateDto.date.toInstant(),
            description = thingCreateDto.description,
            place = thingCreateDto.place.toDomain(),
            owner = user.id!!
        )

        return ResponseEntity.ok(created.toDto(emptyList()))
    }

    override fun getThingById(
        id: String,
        authorization: String
    ): ResponseEntity<ThingDto> {
        val user = tokenService.getUserByHeader(authorization)

        if (user == null || user.id == null) {
            logger.info("User not found for token: $authorization")
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        val thing = thingFinder.findById(id, user.id) ?: throw ThingNotFoundException(id)

        val updated = convertToDto(thing)

        return ResponseEntity.ok(updated)
    }

    override fun getThingByOwner(
        authorization: String,
        page: Int,
        size: Int,
        sort: List<String>?
    ): ResponseEntity<GetThings200Response> {
        val user = tokenService.getUserByHeader(authorization)

        if (user == null) {
            logger.info("User not found for token: $authorization")
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        val page = thingFinder.findByOwner(
            owner = user.id!!,
            pageable = createPageable(page, size, sort)
        )

        return ResponseEntity.ok(
            GetThings200Response(
                page.hints.map { convertToDto(it) },
                page.total.toInt()
            )
        )
    }


    override fun getThings(
        authorization: String,
        thingGetDto: ThingGetDto,
        page: Int,
        size: Int,
        sort: List<String>?
    ): ResponseEntity<GetThings200Response> {
        val user = tokenService.getUserByHeader(authorization)

        if (user == null || user.id == null) {
            logger.info("User not found for token: $authorization")
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        var counter = 0

        val pageable = createPageable(page, size, sort)
        val page = thingFinder.find(
            userId = user.id,
            type = thingGetDto.type?.toDomain(),
            date = thingGetDto.date?.toInstant(),
            place = thingGetDto.place.toDomain(),
            description = thingGetDto.description,
            completed = thingGetDto.completed,
            pageable = pageable
        )

        val converted = page.hints
            .filter {
                if (it.owner != user.id) {
                    true
                } else {
                    counter++
                    false
                }
            }
            .map { convertToDto(it) }

        return ResponseEntity.ok(GetThings200Response(converted, page.total.toInt() - counter))
    }

    override fun responseToThing(
        id: String,
        authorization: String
    ): ResponseEntity<Unit> {
        val user = tokenService.getUserByHeader(authorization)

        if (user == null || user.id == null) {
            logger.info("User not found for token: $authorization")
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        thingRegistry.addResponse(id, user.id)

        return ResponseEntity.ok().build()
    }

    override fun updateThing(
        id: String,
        authorization: String,
        thingDto: ThingDto
    ): ResponseEntity<Unit> {

        val user = tokenService.getUserByHeader(authorization)

        if (user == null) {
            logger.info("User not found for token: $authorization")
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        val toUpdate = thingDto.toDomain(id, owner = user.id!!)

        thingRegistry.update(toUpdate, user.id)

        return ResponseEntity.ok().build()
    }


    override fun uploadPhoto(id: String, authorization: String, version: Long, file: Resource?): ResponseEntity<Unit> {
        val user = tokenService.getUserByHeader(authorization)

        if (user == null || user.id == null) {
            logger.info("User not found for token: $authorization")
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        val thing = thingFinder.findById(id, user.id)

        if (thing?.owner != user.id) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        if (file == null) {
            throw IllegalArgumentException("File is null")
        }

        val fileEncoding = StringUtils.getFilenameExtension(file.filename)

        if (fileEncoding == null) {
            throw IllegalArgumentException("File encoding is null")
        }

        val entity = thingRegistry.addImage(id, version, file.contentAsByteArray, fileEncoding)
        return ResponseEntity.ok().build()
    }

    @ExceptionHandler(ForbiddenModification::class)
    fun handleForbiddenModification(e: ForbiddenModification): ResponseEntity<ErrorResponse?> {
        logger.error("ForbiddenModification: ", e)
        return ResponseEntity.status(403).body(e.toResponse())
    }

    @ExceptionHandler(ThingNotFoundException::class)
    fun handleThingNotFoundException(e: ThingNotFoundException): ResponseEntity<Unit> {
        logger.error("ThingNotFoundException: ", e)
        return ResponseEntity.notFound().build()
    }

    @ExceptionHandler(ThingVersionMismatchException::class)
    fun handleThingVersionMismatchException(e: ThingVersionMismatchException): ResponseEntity<ErrorResponse?> {
        logger.error("ThingVersionMismatchException: ", e)
        return ResponseEntity.status(409).body(e.toResponse())
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(e: IllegalArgumentException): ResponseEntity<ErrorResponse?> {
        logger.error("IllegalArgumentException: ", e)
        return ResponseEntity.badRequest().body(e.toResponse())
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ErrorResponse?> {
        logger.error("Exception: ", e)
        return ResponseEntity.internalServerError().body(e.toResponse())
    }

    private fun convertToDto(thing: Thing): ThingDto {
        val phoneNumbers = thing.responses
            ?.mapNotNull { tokenService.getPhoneNumberById(it) }

        return thing.toDto(phoneNumbers ?: emptyList())
    }
}
package com.maxim.poteryashki.lost.controller

import com.maxim.poteryashki.lost.api.DefaultApiDelegate
import com.maxim.poteryashki.lost.dto.Thing
import com.maxim.poteryashki.lost.dto.ThingCreateDto
import com.maxim.poteryashki.lost.dto.ThingGetDto
import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Component
class DefaultControllerDelegateImpl(

): DefaultApiDelegate {
    override fun createThing(
        authorization: String,
        thingCreateDto: ThingCreateDto
    ): ResponseEntity<Thing> {
        TODO("Not yet implemented")
    }

    override fun getThingById(
        id: String,
        authorization: String
    ): ResponseEntity<Thing> {
        TODO("Not yet implemented")
    }

    override fun getThingByOwner(authorization: String): ResponseEntity<List<Thing>> {
        TODO("Not yet implemented")
    }

    override fun getThings(
        authorization: String,
        thingGetDto: ThingGetDto
    ): ResponseEntity<List<Thing>> {
        TODO("Not yet implemented")
    }

    override fun updateThingStatus(
        id: String,
        authorization: String,
        thingGetDto: ThingGetDto
    ): ResponseEntity<Unit> {
        TODO("Not yet implemented")
    }

    override fun uploadPhoto(
        id: String,
        authorization: String,
        file: Resource?
    ): ResponseEntity<Unit> {
        TODO("Not yet implemented")
    }
}
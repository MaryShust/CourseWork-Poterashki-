package com.maxim.poteryashki.lost.domain.exception

class ThingNotFoundException(
    val thingId: String
): RuntimeException("Thing with id $thingId not found") {
}
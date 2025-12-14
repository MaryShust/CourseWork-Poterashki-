package com.maxim.poteryashki.lost.domain.exception

class ForbiddenModification(
    val thingId: String,
): RuntimeException("Thing $thingId belongs to different user") {
}
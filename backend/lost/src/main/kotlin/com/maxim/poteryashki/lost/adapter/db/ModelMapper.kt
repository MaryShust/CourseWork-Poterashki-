package com.maxim.poteryashki.lost.adapter.db

import com.maxim.poteryashki.lost.domain.ThingType as ThingTypeDomain
import com.maxim.poteryashki.lost.domain.Place as PlaceDomain
import com.maxim.poteryashki.lost.domain.Thing as ThingDomain



fun ThingDomain.toEntity(): ThingEntity =
    ThingEntity(
        id = id,
        owner = owner,
        type = type.toEntity(),
        createdAt = createdAt,
        date = date,
        place = place.toEntity(),
        description = description,
        photos = photos,
        completedAt = completedAt,
        version = version
        )

fun PlaceDomain.toEntity(): Place =
    Place(
        city = city,
        street = street,
        house = house,
        placeName = placeName,
        extraDescription = extraDescription
    )

fun ThingTypeDomain.toEntity(): ThingType =
    when (this) {
        ThingTypeDomain.LOST -> ThingType.LOST
        ThingTypeDomain.FOUND -> ThingType.FOUND
    }

fun ThingEntity.toDomain(): ThingDomain =
    ThingDomain(
        id = id,
        owner = owner,
        type = type.toDomain(),
        createdAt = createdAt,
        date = date,
        place = place.toDomain(),
        description = description,
        photos = photos,
        completedAt = completedAt,
        version = version
        )
fun Place.toDomain(): PlaceDomain =
    PlaceDomain(
        city = city,
        street = street,
        house = house,
        placeName = placeName,
        extraDescription = extraDescription
    )
fun ThingType.toDomain(): ThingTypeDomain =
    when (this) {
        ThingType.LOST -> ThingTypeDomain.LOST
        ThingType.FOUND -> ThingTypeDomain.FOUND
    }


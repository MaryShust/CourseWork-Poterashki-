package com.maxim.poteryashki.lost.controller

import com.maxim.poteryashki.lost.domain.Place
import com.maxim.poteryashki.lost.domain.Thing
import com.maxim.poteryashki.lost.domain.ThingType
import com.maxim.poteryashki.lost.dto.PlaceDto
import com.maxim.poteryashki.lost.dto.ThingDto
import com.maxim.poteryashki.lost.dto.ThingTypeDto
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.net.URI
import java.time.Instant
import java.time.ZoneOffset
import java.util.UUID

fun createPageable(page: Int = 0, size: Int = 20, sort: List<String>? = null): Pageable {
    var pageable = PageRequest.of(page, size)

    if (!sort.isNullOrEmpty()) {
        val sortList = sort.map { sortString ->
            if (sortString.contains(",desc", ignoreCase = true)) {
                Sort.Order.desc(sortString.substringBefore(","))
            } else if (sortString.contains(",asc", ignoreCase = true)) {
                Sort.Order.asc(sortString.substringBefore(","))
            } else {
                Sort.Order.asc(sortString) // default: asc
            }
        }
        pageable = PageRequest.of(page, size, Sort.by(sortList))
    }

    return pageable
}

fun ThingTypeDto.toDomain() =
    when(this) {
        ThingTypeDto.LOST -> ThingType.LOST
        ThingTypeDto.FOUND -> ThingType.FOUND
    }

fun ThingType.toDto() =
    when(this) {
        ThingType.LOST -> ThingTypeDto.LOST
        ThingType.FOUND -> ThingTypeDto.FOUND
    }

fun PlaceDto.toDomain() =
    Place(
        city = this.city,
        street = this.street,
        house = this.house,
        placeName = this.placeName,
        extraDescription = this.extraDescription,
    )

fun Place.toDto() =
    PlaceDto(
        city = this.city,
        street = this.street,
        house = this.house,
        placeName = this.placeName,
        extraDescription = this.extraDescription,
    )

fun Thing.toDto() =
    ThingDto(
        id = this.id,
        description = this.description,
        type = this.type.toDto(),
        place = this.place.toDto(),
        owner = this.owner,
        date = this.date.toOffsetDateTime(),
        photos = this.photos.map { it.toUri() },
        createdAt = this.createdAt.toOffsetDateTime(),
        completedAt = this.completedAt?.toOffsetDateTime(),
        title = title,
        version = this.version,
    )

fun ThingDto.toDomain(id: String, owner: UUID) =
    Thing(
        id = id,
        owner = owner,
        type = this.type.toDomain(),
        createdAt = createdAt?.toInstant() ?: Instant.now(),
        date = date.toInstant(),
        place = place.toDomain(),
        description = description,
        photos = photos.map { it.toString() },
        completedAt = completedAt?.toInstant(),
        title = title,
        version = version,
    )

fun Instant.toOffsetDateTime() =
    this.atOffset(ZoneOffset.UTC)

fun String.toUri() = URI.create(this)

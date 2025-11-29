package com.maxim.poteryashki.lost.db.postgres.place

import org.springframework.data.repository.CrudRepository

interface PlaceRepo: CrudRepository<PlaceEntity, Long> {


}
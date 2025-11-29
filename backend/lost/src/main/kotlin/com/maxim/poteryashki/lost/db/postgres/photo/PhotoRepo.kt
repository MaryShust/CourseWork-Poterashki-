package com.maxim.poteryashki.lost.db.postgres.photo

import org.springframework.data.repository.CrudRepository

interface PhotoRepo: CrudRepository<PhotoEntity, Long> {
}
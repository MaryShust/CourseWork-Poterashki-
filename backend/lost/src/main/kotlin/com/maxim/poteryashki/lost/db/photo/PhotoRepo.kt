package com.maxim.poteryashki.lost.db.photo

import org.springframework.data.repository.CrudRepository

interface PhotoRepo: CrudRepository<PhotoEntity, Long> {
}
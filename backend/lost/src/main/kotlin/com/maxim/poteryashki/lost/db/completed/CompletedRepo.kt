package com.maxim.poteryashki.lost.db.completed

import org.springframework.data.repository.CrudRepository

interface CompletedRepo: CrudRepository<CompletedEntity, Long> {
}
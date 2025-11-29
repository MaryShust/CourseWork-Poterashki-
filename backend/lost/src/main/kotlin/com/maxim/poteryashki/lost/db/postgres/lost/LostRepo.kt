package com.maxim.poteryashki.lost.db.postgres.lost

import org.springframework.data.repository.CrudRepository

interface LostRepo: CrudRepository<LostEntity, Long> {


}
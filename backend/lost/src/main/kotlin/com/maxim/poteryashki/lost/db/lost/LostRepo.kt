package com.maxim.poteryashki.lost.db.lost

import org.springframework.data.repository.CrudRepository

interface LostRepo: CrudRepository<LostEntity, Long> {


}
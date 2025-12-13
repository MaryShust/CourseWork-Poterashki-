package com.maxim.poteryashki.lost.service

import com.maxim.poteryashki.lost.adapter.db.ThingRepository
import com.maxim.poteryashki.lost.adapter.s3.S3Adapter
import org.springframework.stereotype.Service

@Service
class ThingRegistry(
    private val thingRepository: ThingRepository,
    private val s3Adapter: S3Adapter
) {
}
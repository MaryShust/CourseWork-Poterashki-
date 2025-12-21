package com.maxim.poteryashki.lost.service

import com.maxim.poteryashki.lost.domain.Thing
import org.springframework.stereotype.Component


@Component
class ThingMergeService {

    fun mergeResponses(existing: Thing, new: Thing): Thing =
        new.copy(responses = existing.responses)
}
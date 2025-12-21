package com.maxim.poteryashki.auth.controller

import co.elastic.clients.elasticsearch.slm.GetStatsResponse
import com.maxim.poteryashki.auth.domain.Statistics
import com.maxim.poteryashki.auth.domain.User
import com.maxim.poteryashki.auth.dto.GetProfileResponse
import com.maxim.poteryashki.auth.dto.GetStatisticsResponse
import com.maxim.poteryashki.auth.dto.Profile

fun User.toProfileResponse(): GetProfileResponse =
    GetProfileResponse(
        name = name,
        phone = this.metadata?.phone,
        email = email,
        city = this.metadata?.city
    )

fun Statistics.toStatisticsResponse(): GetStatisticsResponse =
    GetStatisticsResponse(
        active = this.active,
        foundTotal = totalFound,
        totalFee = totalFee,
        maxFee = maxFee
    )
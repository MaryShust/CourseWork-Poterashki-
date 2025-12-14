package com.maxim.poteryashki.auth.controller

import com.maxim.poteryashki.auth.domain.User
import com.maxim.poteryashki.auth.dto.GetProfileResponse
import com.maxim.poteryashki.auth.dto.Profile

fun User.toProfileResponse(): GetProfileResponse =
    GetProfileResponse(
        name = name,
        phone = this.metadata?.phone,
        email = email,
        city = this.metadata?.city
    )
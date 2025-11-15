package com.maxim.poteryashki.gateway

import com.maxim.poteryashki.gateway.service.UserAuthService
import org.springframework.cloud.gateway.server.mvc.filter.HttpHeadersFilter
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.servlet.function.ServerRequest

@Component
class UserHeaderFilter(
    private val userAuthService: UserAuthService
): HttpHeadersFilter.RequestHttpHeadersFilter {

    override fun apply(
        headers: HttpHeaders?,
        request: ServerRequest?
    ): HttpHeaders? {

        val userID = headers
            ?.getFirst("Authorization")
            ?.removePrefix()
            ?.let { userAuthService.getUserIdByToken(it) }
            ?: throw RuntimeException("User not found")

        headers.add("X-User-Id", userID.toString())
        return headers
    }

    private fun String.removePrefix() =
        this.replace("Bearer", "")
}
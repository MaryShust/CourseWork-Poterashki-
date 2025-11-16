package com.maxim.poteryashki.gateway.service.security


import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.servlet.function.HandlerFilterFunction
import org.springframework.web.servlet.function.HandlerFunction
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse

@Component
class AuthGatewayFilter(
    private val tokenService: TokenService
) : HandlerFilterFunction<ServerResponse, ServerResponse> {

    override fun filter(
        request: ServerRequest,
        next: HandlerFunction<ServerResponse?>
    ): ServerResponse {
        val headers = request.headers()

        val token = headers.firstHeader(HttpHeaders.AUTHORIZATION)?.trim()
            ?.takeIf { it.startsWith("Bearer ", ignoreCase = true) }
            ?.substring(7)
            ?.trim()

        // Нет токена — сразу 403
        if (token.isNullOrEmpty()) {
            return ServerResponse.status(HttpStatus.UNAUTHORIZED).build()
        }

        // Валидируем токен
        val user = tokenService.getUserByToken(token)
        if (user == null) {
            return ServerResponse.status(HttpStatus.UNAUTHORIZED).build()
        }

        val modified = ServerRequest.from(request)
            .headers {
                it.set("X-User-Id", user.id.toString())
                it.set("X-User-Role", user.userMetadata.role.name)
            }
           .build()

        return next.handle(modified)
    }


}
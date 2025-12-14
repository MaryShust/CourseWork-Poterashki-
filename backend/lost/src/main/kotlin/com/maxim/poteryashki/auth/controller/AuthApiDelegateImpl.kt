package com.maxim.poteryashki.auth.controller

import com.maxim.poteryashki.auth.api.AuthApiDelegate
import com.maxim.poteryashki.auth.domain.UserMetadata
import com.maxim.poteryashki.auth.dto.GetProfileResponse
import com.maxim.poteryashki.auth.dto.GetTokenResponse
import com.maxim.poteryashki.auth.dto.LoginRequest
import com.maxim.poteryashki.auth.dto.Profile
import com.maxim.poteryashki.auth.dto.RegisterRequest
import com.maxim.poteryashki.auth.service.TokenService
import com.maxim.poteryashki.auth.service.UserRegistry
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

private val logger = LoggerFactory.getLogger(AuthApiDelegateImpl::class.java)

@Service
class AuthApiDelegateImpl(
    private val userRegistry: UserRegistry,
    private val tokenService: TokenService,
): AuthApiDelegate {

    override fun login(loginRequest: LoginRequest): ResponseEntity<GetTokenResponse> {
        val userId = userRegistry.getUser(
            email = loginRequest.email,
            password = loginRequest.password,
        )

        if (userId == null) {
            logger.debug("User not found for login request: $loginRequest")
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        val token = tokenService.generateToken(userId)

        return ResponseEntity.ok(GetTokenResponse(token))
    }

    override fun register(registerRequest: RegisterRequest): ResponseEntity<GetTokenResponse> {
        val userId = userRegistry.createUser(
            username = registerRequest.username,
            email = registerRequest.email,
            password = registerRequest.password,
        )

        val token = tokenService.generateToken(userId)

        return ResponseEntity.ok(GetTokenResponse(token))
    }

}
package com.maxim.poteryashki.auth.controller

import com.maxim.poteryashki.auth.api.PublicApiDelegate
import com.maxim.poteryashki.auth.domain.UserMetadata
import com.maxim.poteryashki.auth.dto.GetProfileResponse
import com.maxim.poteryashki.auth.dto.GetTokenResponse
import com.maxim.poteryashki.auth.dto.LoginRequest
import com.maxim.poteryashki.auth.dto.Profile
import com.maxim.poteryashki.auth.dto.RegisterRequest
import com.maxim.poteryashki.auth.service.TokenService
import com.maxim.poteryashki.auth.service.UserRegistry
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class PublicApiDelegateImpl(
    private val userRegistry: UserRegistry,
    private val tokenService: TokenService,
): PublicApiDelegate {

    override fun getProfile(authorization: String): ResponseEntity<GetProfileResponse> {
        val user = tokenService.getUserByToken(authorization)

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        return ResponseEntity.ok(user.toProfileResponse())
    }

    override fun login(loginRequest: LoginRequest): ResponseEntity<GetTokenResponse> {
        val userId = userRegistry.getUser(
            email = loginRequest.email,
            password = loginRequest.password,
        )

        if (userId == null) {
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

    override fun updateProfile(authorization: String, profile: Profile): ResponseEntity<Unit> {
        val user = tokenService.getUserByToken(authorization)

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        user.copy(
            email = profile.email,
            metadata = UserMetadata.basic(
                name = profile.name,
                city = profile.city,
                phone = profile.phone
            )
        )

        userRegistry.update(user)

        return ResponseEntity.ok().build()

    }

}
package com.maxim.poteryashki.auth.controller

import com.maxim.poteryashki.auth.api.ProfileApiDelegate
import com.maxim.poteryashki.auth.domain.UserMetadata
import com.maxim.poteryashki.auth.dto.GetProfileResponse
import com.maxim.poteryashki.auth.dto.Profile
import com.maxim.poteryashki.auth.service.TokenService
import com.maxim.poteryashki.auth.service.UserRegistry
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import java.util.UUID

private val logger = LoggerFactory.getLogger(ProfileApiDelegateImpl::class.java)

@Component
class ProfileApiDelegateImpl(
    private val userRegistry: UserRegistry,
    private val tokenService: TokenService,
): ProfileApiDelegate{
    override fun getProfile(authorization: String): ResponseEntity<GetProfileResponse> {
        val user = tokenService.getUserByHeader(authorization.trim().removePrefix("Bearer "))

        if (user == null) {
            logger.debug("User not found for token: $authorization")
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        return ResponseEntity.ok(user.toProfileResponse())
    }

    override fun getProfileForUser(
        id: UUID,
        authorization: String
    ): ResponseEntity<GetProfileResponse> {
        val authUser = tokenService.getUserByHeader(authorization)

        if (authUser == null) {
            logger.debug("User not found for token: $authorization")
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        val user = userRegistry.getUserById(id)

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }

        return ResponseEntity.ok(user.toProfileResponse())
    }

    override fun updateProfile(authorization: String, profile: Profile): ResponseEntity<Unit> {
        val user = tokenService.getUserByHeader(authorization)

        if (user == null) {
            logger.debug("User not found for token: $authorization")
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        val toUpdate = user.copy(
            email = profile.email,
            metadata = UserMetadata.basic(
                name = profile.name,
                city = profile.city,
                phone = profile.phone
            )
        )

        userRegistry.update(toUpdate)

        return ResponseEntity.ok().build()
    }
}
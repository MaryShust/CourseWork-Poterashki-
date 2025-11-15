package com.maxim.poteryashki.gateway.db.user

import com.fasterxml.jackson.databind.ObjectMapper
import com.maxim.poteryashki.gateway.domain.Role
import com.maxim.poteryashki.gateway.domain.UserMetadata
import com.maxim.poteryashki.gateway.domain.User
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class UserDao(
    private val objectMapper: ObjectMapper,
    private val userRepository: UserRepository
) {

    fun create(user: User): UUID = userRepository
        .create(
            user.name,
            user.password,
            user.role.name,
            objectMapper.writeValueAsString(user.userMetadata)
        )

    fun update(user: User) =
        userRepository.update(
            user.id!!,
            user.name,
            user.password,
            user.role.name,
            objectMapper.writeValueAsString(user.userMetadata)
        )

    fun getById(id: UUID) =
        userRepository.getUserDbById(id)?.toDomain()

    fun getByName(name: String) =
        userRepository.getUserDbByName(name)?.toDomain()


    private fun UserDb.toDomain() = User(
        id,
        name,
        password,
        Role.valueOf(role),
        objectMapper.readValue(metadata, UserMetadata::class.java)
    )


}
package com.maxim.poteryashki.auth.db.user

import com.fasterxml.jackson.databind.ObjectMapper
import com.maxim.poteryashki.auth.domain.UserMetadata
import com.maxim.poteryashki.auth.domain.User
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
            objectMapper.writeValueAsString(user.metadata)
        )

    fun update(user: User) =
        userRepository.update(
            user.id!!,
            user.name,
            user.password,
            objectMapper.writeValueAsString(user.metadata)
        )

    fun getById(id: UUID) =
        userRepository.getUserDbById(id)?.toDomain()

    fun getByEmail(email: String, password: String) =
        userRepository.getByEmailAndPassword(email, password)?.toDomain()

    private fun UserDb.toDomain() = User(
        id,
        name,
        email,
        password,
        objectMapper.readValue(metadata, UserMetadata::class.java)
    )


}
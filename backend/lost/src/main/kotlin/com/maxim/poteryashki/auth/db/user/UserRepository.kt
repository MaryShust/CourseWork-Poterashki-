package com.maxim.poteryashki.auth.db.user

import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.UUID

@Repository
interface UserRepository : CrudRepository<UserDb, UUID> {

    @Modifying
    @Query(
        """UPDATE users SET 
        username = :name,
        email = :email,
        password = :password,
        data = :metadata
        WHERE id = :id
    """
    )
    fun update(id: UUID, name: String, email: String, password: String, metadata: String)

    @Query(
        """
    INSERT INTO users (username, email, password, data, created_at) 
    VALUES (:name, :email, :password, :metadata, :createdAt) 
    RETURNING id;
    """
    )
    fun create(name: String, email: String, password: String, metadata: String, createdAt: Instant): UUID

    @Query(
        """
        SELECT * FROM users WHERE id = :id
    """
    )
    fun getUserDbById(id: UUID): UserDb?

    @Query(
        """
        SELECT * FROM users WHERE email = :email AND password = :password
    """
    )
    fun getByEmailAndPassword(email: String, password: String): UserDb?
}
package com.maxim.poteryashki.auth.db.user

import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserRepository : CrudRepository<UserDb, UUID> {

    @Modifying
    @Query("""UPDATE users SET 
        name = :name,
        password = :password,
        metadata = :metadata
        WHERE id = :id
    """)
    fun update(id: UUID, name: String, password: String, metadata: String)

    @Modifying
    @Query(""" INSERT INTO users(name, password, metadata) 
        VALUES (:name, :password, :metadata) RETURNING id;
    """)
    fun create(name: String, password: String, metadata: String): UUID

    @Query("""
        SELECT * FROM users WHERE id = :id
    """)
    fun getUserDbById(id: UUID): UserDb?

    @Query("""
        SELECT * FROM users WHERE email = :email AND password = :password
    """)
    fun getByEmailAndPassword(email: String, password: String): UserDb?
}
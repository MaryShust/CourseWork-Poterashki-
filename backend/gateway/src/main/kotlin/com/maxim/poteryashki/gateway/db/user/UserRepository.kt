package com.maxim.poteryashki.gateway.db.user

import com.maxim.poteryashki.gateway.domain.User
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserRepository : CrudRepository<UserDb, UUID> {

    @Modifying
    @Query("""UPDATE user SET 
        name = :name,
        password = :password,
        role = :role,
        metadata = :metadata
        WHERE id = :id
    """)
    fun update(id: UUID, name: String, password: String, role:String, metadata: String)

    @Modifying
    @Query(""" INSERT INTO user(name, password, metadata) 
        VALUES (:name, :password, :metadata) RETURNING id;
    """)
    fun create(name: String, password: String, role: String, metadata: String): UUID

    @Query("""
        SELECT * FROM user WHERE id = :id
    """)
    fun getUserDbById(id: UUID): UserDb?

    @Query("""
        SELECT * FROM user WHERE name = :name
    """)
    fun getUserDbByName(name: String): UserDb?
}
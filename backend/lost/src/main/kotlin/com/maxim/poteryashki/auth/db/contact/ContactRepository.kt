package com.maxim.poteryashki.auth.db.contact

import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ContactRepository : CrudRepository<ContactDb, Long> {

    @Modifying
    @Query("UPDATE contact SET data = :data WHERE id = :id")
    fun update(id: Long, data: String)

    @Modifying
    @Query("INSERT INTO contact(user_id, data) VALUES (:userId, :data) RETURNING id")
    fun insert(userId: UUID, data: String): Long

    @Modifying
    @Query("DELETE FROM contact WHERE id = :id")
    fun removeByIdAndUserId(id: Long)

    @Modifying
    @Query("DELETE FROM contact WHERE user_id = :userId")
    fun removeAllByUserId(userId: UUID): Int

    @Query("SELECT * FROM contact WHERE user_id = :userId")
    fun getByUserId(userId: UUID): List<ContactDb>
}
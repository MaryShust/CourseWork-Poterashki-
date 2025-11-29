package com.maxim.poteryashki.notification.db.task

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.Repository
import java.time.Instant


interface TaskRepo: Repository<TaskEntity, Long> {

    @Query("INSERT INTO task (foundOwner, lostOwner, startTime, status) VALUES (:foundOwner, :lostOwner, :startTime, :status) RETURNING id")
    fun create(foundOwner: String, lostOwner: String, startTime: Instant, status: String): Long

    @Query("SELECT * FROM task WHERE id = :id")
    fun findById(id: Long): TaskEntity?

    @Query("UPDATE task SET status = :status WHERE id = :id")
    fun updateStatus(id: Long, status: String)

}
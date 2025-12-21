package com.maxim.poteryashki.auth.db.statistics

import com.maxim.poteryashki.auth.domain.Statistics
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface StatisticsRepo : CrudRepository<StatisticsDb, UUID> {

    @Query("SELECT * FROM statistics WHERE user_id = :userId")
    fun getByUserId(userId: UUID): StatisticsDb?

    @Modifying
    @Query(
        """
        INSERT INTO statistics (user_id, active, total_found, total_fee, max_fee)
        VALUES (:userId, :active, :totalFound, :totalFee, :maxFee)
        ON CONFLICT (user_id) DO UPDATE SET
            active = EXCLUDED.active,
            total_found = EXCLUDED.total_found,
            total_fee = EXCLUDED.total_fee,
            max_fee = EXCLUDED.max_fee
    """
    )
    fun upsert(userId: UUID, active: Int, totalFound: Int, totalFee: Int, maxFee: Int)

}
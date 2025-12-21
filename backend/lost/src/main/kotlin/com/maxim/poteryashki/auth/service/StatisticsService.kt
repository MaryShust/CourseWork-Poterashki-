package com.maxim.poteryashki.auth.service

import com.maxim.poteryashki.auth.db.statistics.StatisticsDao
import com.maxim.poteryashki.auth.domain.Statistics
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import kotlin.math.max

@Service
class StatisticsService(
    private val statisticsDao: StatisticsDao
) {

    fun getStatistics(userId: UUID): Statistics? {
        return statisticsDao.getStatistics(userId)
    }

    @Transactional
    fun incrementActive(userId: UUID) {
        val before = statisticsDao.getStatistics(userId)
        if (before == null) {
            throw IllegalArgumentException("User not found")
        }

        statisticsDao.save(
            before.copy(active = before.active + 1),
            userId
        )
    }

    @Transactional
    fun decrementActive(userId: UUID) {
        val before = statisticsDao.getStatistics(userId)
        if (before == null) {
            throw IllegalArgumentException("User not found")
        }

        statisticsDao.save(
            before.copy(active = before.active - 1),
            userId
        )
    }

    @Transactional
    fun incrementFound(userId: UUID) {
        val before = statisticsDao.getStatistics(userId)
        if (before == null) {
            throw IllegalArgumentException("User not found")
        }

        statisticsDao.save(
            before.copy(totalFound = before.totalFound + 1),
            userId
        )
    }

    @Transactional
    fun addFee(fee: Int, userId: UUID) {
        val before = statisticsDao.getStatistics(userId)
        if (before == null) {
            throw IllegalArgumentException("User not found")
        }

        statisticsDao.save(
            before.copy(
                totalFee = before.totalFee + fee,
                maxFee = max(before.maxFee, fee)
            ),
            userId
        )
    }
}
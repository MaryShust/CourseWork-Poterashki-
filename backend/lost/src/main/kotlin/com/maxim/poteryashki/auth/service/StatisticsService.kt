package com.maxim.poteryashki.auth.service

import com.maxim.poteryashki.auth.db.statistics.StatisticsDao
import com.maxim.poteryashki.auth.domain.Statistics
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class StatisticsService(
    private val statisticsDao: StatisticsDao
) {

    @Transactional
    fun incrementActive(userId: UUID) {
        val before = statisticsDao.getStatistics(userId)
        if (before == null) {
            throw IllegalArgumentException("User not found")
        }

        statisticsDao.save(
            Statistics(
                active = before.active + 1,
                totalFound = before.totalFound
            ),
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
            Statistics(
                active = before.active - 1,
                totalFound = before.totalFound
            ),
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
            Statistics(
                active = before.active,
                totalFound = before.totalFound + 1
            ),
            userId
        )
    }
}
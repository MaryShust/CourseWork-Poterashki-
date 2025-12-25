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
        var before = statisticsDao.getStatistics(userId)
        if (before == null) {
            before = initializeEmpty(userId)
        }

        statisticsDao.save(
            before.copy(active = before.active + 1),
            userId
        )
    }

    @Transactional
    fun decrementActive(userId: UUID) {
        var before = statisticsDao.getStatistics(userId)
        if (before == null) {
            before = initializeEmpty(userId)
        }

        statisticsDao.save(
            before.copy(active = before.active - 1),
            userId
        )
    }

    @Transactional
    fun incrementFound(userId: UUID) {
        var before = statisticsDao.getStatistics(userId)
        if (before == null) {
            before = initializeEmpty(userId)
        }

        statisticsDao.save(
            before.copy(totalFound = before.totalFound + 1),
            userId
        )
    }

    @Transactional
    fun addFee(fee: Int, userId: UUID) {
        var before = statisticsDao.getStatistics(userId)
        if (before == null) {
            before = initializeEmpty(userId)
        }

        statisticsDao.save(
            before.copy(
                totalFee = before.totalFee + fee,
                maxFee = max(before.maxFee, fee)
            ),
            userId
        )
    }

    private fun initializeEmpty(userId: UUID): Statistics {
        val statistics = Statistics(
            active = 0,
            totalFound = 0,
            totalFee = 0,
            maxFee = 0
        )

        return statistics
    }
}
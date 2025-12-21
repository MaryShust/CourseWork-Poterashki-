package com.maxim.poteryashki.auth.db.statistics

import com.maxim.poteryashki.auth.domain.Statistics
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class StatisticsDao(
    private val statisticsRepo: StatisticsRepo
) {

    fun getStatistics(userId: UUID): Statistics? {
        return statisticsRepo.getByUserId(userId)?.toDomain()
    }

    fun save(statistics: Statistics, userId: UUID) {
        statisticsRepo.upsert(
            userId, statistics.active, statistics.totalFound,
            statistics.totalFee, statistics.maxFee
        )
    }
}

fun StatisticsDb.toDomain(): Statistics {
    return Statistics(
        active = active,
        totalFound = totalFound,
        totalFee = totalFee,
        maxFee = maxFee
    )
}

fun Statistics.toDb(userId: UUID): StatisticsDb {
    return StatisticsDb(
        userId = userId,
        active = active,
        totalFound = totalFound,
        totalFee = totalFee,
        maxFee = maxFee
    )
}

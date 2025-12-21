package com.maxim.poteryashki.auth.db.statistics

import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table
data class StatisticsDb (
    val userId: UUID,
    val active: Int,
    val totalFound: Int
)

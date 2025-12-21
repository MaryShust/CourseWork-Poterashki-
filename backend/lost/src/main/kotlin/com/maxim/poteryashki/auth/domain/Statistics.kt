package com.maxim.poteryashki.auth.domain

data class Statistics(
    val active: Int,
    val totalFound: Int,
    val totalFee: Int,
    val maxFee: Int
)
package com.freshkeeper.model

data class Statistics(
    val totalWaste: Int,
    val averageWaste: Float,
    val daysWithoutWaste: Int,
    val mostWastedItems: List<FoodItem>,
    val wasteReduction: Int,
    val usedItemsPercentage: Int,
    val mostWastedCategory: String,
    val expiredDates: List<Long>,
)

package com.freshkeeper.model

data class Statistics(
    val totalWaste: Int,
    val averageWaste: Int,
    val daysWithoutWaste: Int,
    val mostWastedItems: List<FoodItem>,
    val wasteReduction: Int,
)

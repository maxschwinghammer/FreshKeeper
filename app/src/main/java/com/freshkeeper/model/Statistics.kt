package com.freshkeeper.model

data class Statistics(
    val totalWaste: Int,
    val averageWaste: Float,
    val daysWithoutWaste: Int,
    val mostWastedItems: List<FoodItem>,
    val wasteReduction: Int,
    val usedItemsPercentage: Int,
    val mostWastedCategory: String,
    val discardedDates: List<Long>,
    val averageNutriments: Nutriments? = null,
    val averageNutriScore: String? = null,
)

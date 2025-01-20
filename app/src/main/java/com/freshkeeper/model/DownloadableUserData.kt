package com.freshkeeper.model

data class DownloadableUserData(
    val user: User,
    val activities: List<Activity> = emptyList(),
    val foodItems: List<FoodItem> = emptyList(),
    val household: Household? = null,
)

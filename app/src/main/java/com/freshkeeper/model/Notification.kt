package com.freshkeeper.model

data class Notification(
    val title: String,
    val id: String,
    val type: String,
    val destinationScreen: String,
    val description: String,
    val buttonTextId: Int,
    val imageResId: Int,
    val householdId: String? = null,
)

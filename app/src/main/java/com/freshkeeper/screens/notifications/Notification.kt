package com.freshkeeper.screens.notifications

data class Notification(
    val title: String,
    val destinationScreen: String,
    val description: String,
    val buttonText: String,
    val imageResId: Int,
)

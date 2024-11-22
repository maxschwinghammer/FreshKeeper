package com.freshkeeper.screens.notifications

data class Notification(
    val title: String,
    val destinationScreen: String,
    val description: String,
    val buttonTextId: Int,
    val imageResId: Int,
)

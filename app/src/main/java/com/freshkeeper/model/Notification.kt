package com.freshkeeper.model

data class Notification(
    val title: NotificationText = NotificationText(),
    val id: String = "",
    val type: String = "",
    val destinationScreen: String = "",
    val description: NotificationText = NotificationText(),
    val buttonText: NotificationText = NotificationText(),
    val imageResId: Int = 0,
)

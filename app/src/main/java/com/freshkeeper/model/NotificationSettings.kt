package com.freshkeeper.model

import java.time.LocalTime

data class NotificationSettings(
    val userId: String = "",
    val dailyNotificationTime: String = LocalTime.of(12, 0).toString(),
    val timeBeforeExpiration: Int = 2,
    val dailyReminders: Boolean = false,
    val foodAdded: Boolean = false,
    val householdChanges: Boolean = false,
    val foodExpiring: Boolean = false,
    val tips: Boolean = false,
    val statistics: Boolean = false,
)

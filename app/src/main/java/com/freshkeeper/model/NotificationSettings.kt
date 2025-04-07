package com.freshkeeper.model

import java.time.LocalTime

data class NotificationSettings(
    val dailyNotificationTime: String = LocalTime.of(12, 0).toString(),
    val dailyReminders: Boolean = false,
    val foodAdded: Boolean = false,
    val foodExpiring: Boolean = false,
    val householdChanges: Boolean = false,
    val statistics: Boolean = false,
    val timeBeforeExpiration: Int = 2,
    val tips: Boolean = false,
)

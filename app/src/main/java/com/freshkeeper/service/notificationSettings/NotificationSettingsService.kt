package com.freshkeeper.service.notificationSettings

import com.freshkeeper.model.NotificationSettings
import com.freshkeeper.model.User

interface NotificationSettingsService {
    suspend fun getUser(): User

    suspend fun getNotificationSettings(): NotificationSettings?

    suspend fun updateNotificationSettingsField(
        field: String,
        value: Any,
    )
}

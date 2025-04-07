package com.freshkeeper.service.notification

import com.freshkeeper.model.Notification

interface NotificationService {
    suspend fun getAllNotifications(): List<Notification>

    suspend fun deleteNotificationById(notificationId: String): Boolean
}

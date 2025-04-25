package com.freshkeeper.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import com.freshkeeper.service.notification.scheduleDailyReminder
import com.google.firebase.firestore.FirebaseFirestore

class NotificationManager(
    private val context: Context,
) {
    fun setupNotificationChannel() {
        val channel =
            NotificationChannel(
                "default",
                "Standard notifications",
                NotificationManager.IMPORTANCE_HIGH,
            )
        val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    fun scheduleReminderIfEnabled(
        userId: String?,
        firestore: FirebaseFirestore,
    ) {
        if (userId == null) return
        firestore
            .collection("notificationSettings")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val doc = documents.first()
                    if (doc.getBoolean("dailyReminders") == true) {
                        doc.getString("dailyNotificationTime")?.let {
                            scheduleDailyReminder(context, it)
                        }
                    }
                }
            }
    }
}

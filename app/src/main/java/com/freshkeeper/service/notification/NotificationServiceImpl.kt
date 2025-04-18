package com.freshkeeper.service.notification

import android.util.Log
import com.freshkeeper.model.Notification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NotificationServiceImpl
    @Inject
    constructor() : NotificationService {
        private val firestore = FirebaseFirestore.getInstance()
        private val userId = FirebaseAuth.getInstance().currentUser?.uid

        override suspend fun getAllNotifications(): List<Notification> {
            if (userId == null) return emptyList()

            return try {
                val snapshot =
                    firestore
                        .collection("users")
                        .document(userId)
                        .collection("notifications")
                        .get()
                        .await()

                snapshot.documents.mapNotNull { doc ->
                    doc.toObject<Notification>()?.copy(id = doc.id)
                }
            } catch (e: Exception) {
                Log.e("NotificationService", "Error getting notifications: ${e.message}")
                emptyList()
            }
        }

        override suspend fun deleteNotificationById(notificationId: String): Boolean {
            if (userId == null) return false

            return try {
                firestore
                    .collection("users")
                    .document(userId)
                    .collection("notifications")
                    .document(notificationId)
                    .delete()
                    .await()
                true
            } catch (e: Exception) {
                Log.e("NotificationService", "Error deleting notification: ${e.message}")
                false
            }
        }
    }

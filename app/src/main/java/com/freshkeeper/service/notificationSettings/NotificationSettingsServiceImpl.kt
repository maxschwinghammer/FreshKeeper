package com.freshkeeper.service.notificationSettings

import com.freshkeeper.model.NotificationSettings
import com.freshkeeper.model.User
import com.freshkeeper.service.account.AccountService
import com.google.firebase.firestore.FirebaseFirestore
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.tasks.await

@Singleton
class NotificationSettingsServiceImpl
    @Inject
    constructor(
        private val accountService: AccountService,
        private val firestore: FirebaseFirestore,
    ) : NotificationSettingsService {
        override suspend fun getUser(): User = accountService.getUserObject()

        override suspend fun getNotificationSettings(): NotificationSettings? {
            val userId = getUser().id
            val snapshot =
                firestore
                    .collection("notificationSettings")
                    .document(userId)
                    .get()
                    .await()
            return snapshot.toObject(NotificationSettings::class.java)
        }

        override suspend fun updateNotificationSettingsField(
            field: String,
            value: Any,
        ) {
            val userId = getUser().id
            firestore
                .collection("notificationSettings")
                .document(userId)
                .update(field, value)
                .await()
        }
    }

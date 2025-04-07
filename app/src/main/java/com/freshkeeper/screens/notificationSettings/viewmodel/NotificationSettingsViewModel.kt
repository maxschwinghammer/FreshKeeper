package com.freshkeeper.screens.notificationSettings.viewmodel

import com.freshkeeper.model.NotificationSettings
import com.freshkeeper.model.User
import com.freshkeeper.screens.AppViewModel
import com.freshkeeper.service.account.AccountService
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class NotificationSettingsViewModel
    @Inject
    constructor(
        private val accountService: AccountService,
        private val firestore: FirebaseFirestore,
    ) : AppViewModel() {
        private val _user = MutableStateFlow(User())
        val user: StateFlow<User> = _user.asStateFlow()

        private val _notificationSettings = MutableStateFlow<NotificationSettings?>(null)
        val notificationSettings: StateFlow<NotificationSettings?> =
            _notificationSettings
                .asStateFlow()

        init {
            launchCatching {
                _user.value = accountService.getUserObject()
                getNotificationSettings()
            }
        }

        private fun getNotificationSettings() {
            launchCatching {
                val userId = _user.value.id
                val snapshot =
                    firestore
                        .collection("notificationSettings")
                        .document(userId)
                        .get()
                        .await()

                val settings = snapshot?.toObject(NotificationSettings::class.java)
                _notificationSettings.value = settings
            }
        }

        fun updateDailyNotificationTime(time: LocalTime) {
            launchCatching {
                val userId = _user.value.id
                val docRef =
                    firestore
                        .collection("notificationSettings")
                        .document(userId)
                        .get()
                        .await()
                        ?.reference

                docRef?.update("dailyNotificationTime", time.toString())
                _notificationSettings.value =
                    _notificationSettings.value?.copy(dailyNotificationTime = time.toString())
            }
        }

        fun updateTimeBeforeExpiration(value: Int) {
            launchCatching {
                val userId = _user.value.id
                val docRef =
                    firestore
                        .collection("notificationSettings")
                        .document(userId)
                        .get()
                        .await()
                        ?.reference

                docRef?.update("timeBeforeExpiration", value)
                _notificationSettings.value =
                    _notificationSettings.value?.copy(timeBeforeExpiration = value)
            }
        }

        fun updateDailyReminders(isChecked: Boolean) {
            launchCatching {
                val userId = _user.value.id
                val docRef =
                    firestore
                        .collection("notificationSettings")
                        .document(userId)
                        .get()
                        .await()
                        ?.reference

                docRef?.update("dailyReminders", isChecked)
                _notificationSettings.value =
                    _notificationSettings.value?.copy(dailyReminders = isChecked)
            }
        }

        fun updateFoodAdded(isChecked: Boolean) {
            launchCatching {
                val userId = _user.value.id
                val docRef =
                    firestore
                        .collection("notificationSettings")
                        .document(userId)
                        .get()
                        .await()
                        ?.reference

                docRef?.update("foodAdded", isChecked)
                _notificationSettings.value =
                    _notificationSettings.value?.copy(foodAdded = isChecked)
            }
        }

        fun updateHouseholdChanges(isChecked: Boolean) {
            launchCatching {
                val userId = _user.value.id
                val docRef =
                    firestore
                        .collection("notificationSettings")
                        .document(userId)
                        .get()
                        .await()
                        ?.reference

                docRef?.update("householdChanges", isChecked)
                _notificationSettings.value =
                    _notificationSettings.value?.copy(householdChanges = isChecked)
            }
        }

        fun updateFoodExpiring(isChecked: Boolean) {
            launchCatching {
                val userId = _user.value.id
                val docRef =
                    firestore
                        .collection("notificationSettings")
                        .document(userId)
                        .get()
                        .await()
                        ?.reference

                docRef?.update("foodExpiring", isChecked)
                _notificationSettings.value =
                    _notificationSettings.value?.copy(foodExpiring = isChecked)
            }
        }

        fun updateTips(isChecked: Boolean) {
            launchCatching {
                val userId = _user.value.id
                val docRef =
                    firestore
                        .collection("notificationSettings")
                        .document(userId)
                        .get()
                        .await()
                        ?.reference

                docRef?.update("tips", isChecked)
                _notificationSettings.value =
                    _notificationSettings.value?.copy(tips = isChecked)
            }
        }

        fun updateStatistics(isChecked: Boolean) {
            launchCatching {
                val userId = _user.value.id
                val docRef =
                    firestore
                        .collection("notificationSettings")
                        .document(userId)
                        .get()
                        .await()
                        ?.reference

                docRef?.update("statistics", isChecked)
                _notificationSettings.value =
                    _notificationSettings.value?.copy(statistics = isChecked)
            }
        }
    }

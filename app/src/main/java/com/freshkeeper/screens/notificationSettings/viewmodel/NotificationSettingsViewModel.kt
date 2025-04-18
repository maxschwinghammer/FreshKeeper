package com.freshkeeper.screens.notificationSettings.viewmodel

import com.freshkeeper.model.NotificationSettings
import com.freshkeeper.model.User
import com.freshkeeper.screens.AppViewModel
import com.freshkeeper.service.notificationSettings.NotificationSettingsService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class NotificationSettingsViewModel
    @Inject
    constructor(
        private val service: NotificationSettingsService,
    ) : AppViewModel() {
        private val _user = MutableStateFlow(User())
        val user: StateFlow<User> = _user.asStateFlow()

        private val _notificationSettings = MutableStateFlow<NotificationSettings?>(null)
        val notificationSettings: StateFlow<NotificationSettings?> = _notificationSettings.asStateFlow()

        init {
            launchCatching {
                _user.value = service.getUser()
                _notificationSettings.value = service.getNotificationSettings()
            }
        }

        fun updateDailyNotificationTime(time: LocalTime) =
            launchCatching {
                service.updateNotificationSettingsField("dailyNotificationTime", time.toString())
                _notificationSettings.value =
                    _notificationSettings.value?.copy(dailyNotificationTime = time.toString())
            }

        fun updateTimeBeforeExpiration(value: Int) =
            launchCatching {
                service.updateNotificationSettingsField("timeBeforeExpiration", value)
                _notificationSettings.value =
                    _notificationSettings.value?.copy(timeBeforeExpiration = value)
            }

        fun updateDailyReminders(isChecked: Boolean) =
            launchCatching {
                service.updateNotificationSettingsField("dailyReminders", isChecked)
                _notificationSettings.value =
                    _notificationSettings.value?.copy(dailyReminders = isChecked)
            }

        fun updateFoodAdded(isChecked: Boolean) =
            launchCatching {
                service.updateNotificationSettingsField("foodAdded", isChecked)
                _notificationSettings.value =
                    _notificationSettings.value?.copy(foodAdded = isChecked)
            }

        fun updateHouseholdChanges(isChecked: Boolean) =
            launchCatching {
                service.updateNotificationSettingsField("householdChanges", isChecked)
                _notificationSettings.value =
                    _notificationSettings.value?.copy(householdChanges = isChecked)
            }

        fun updateFoodExpiring(isChecked: Boolean) =
            launchCatching {
                service.updateNotificationSettingsField("foodExpiring", isChecked)
                _notificationSettings.value =
                    _notificationSettings.value?.copy(foodExpiring = isChecked)
            }

        fun updateTips(isChecked: Boolean) =
            launchCatching {
                service.updateNotificationSettingsField("tips", isChecked)
                _notificationSettings.value =
                    _notificationSettings.value?.copy(tips = isChecked)
            }

        fun updateStatistics(isChecked: Boolean) =
            launchCatching {
                service.updateNotificationSettingsField("statistics", isChecked)
                _notificationSettings.value =
                    _notificationSettings.value?.copy(statistics = isChecked)
            }
    }

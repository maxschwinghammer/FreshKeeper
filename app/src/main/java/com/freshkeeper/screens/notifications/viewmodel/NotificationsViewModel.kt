package com.freshkeeper.screens.notifications.viewmodel

import androidx.lifecycle.viewModelScope
import com.freshkeeper.model.Notification
import com.freshkeeper.screens.AppViewModel
import com.freshkeeper.service.notification.NotificationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel
    @Inject
    constructor(
        private val notificationService: NotificationService,
    ) : AppViewModel() {
        private val _badgeCount = MutableStateFlow(0)
        val badgeCount: StateFlow<Int> = _badgeCount

        private val _hasNews = MutableStateFlow(false)
        val hasNews: StateFlow<Boolean> = _hasNews

        private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
        val notifications: StateFlow<List<Notification>> = _notifications

        init {
            getNotifications()
        }

        private fun getNotifications() {
            viewModelScope.launch {
                _notifications.value = notificationService.getAllNotifications()
            }
        }

        fun removeNotification(notificationId: String) {
            _notifications.update { list ->
                list.filterNot { it.id == notificationId }
            }
            viewModelScope.launch {
                notificationService.deleteNotificationById(notificationId)
            }
        }
    }

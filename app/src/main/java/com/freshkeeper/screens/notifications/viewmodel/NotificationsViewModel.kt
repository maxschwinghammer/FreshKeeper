package com.freshkeeper.screens.notifications.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freshkeeper.R
import com.freshkeeper.model.Notification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel
    @Inject
    constructor() : ViewModel() {
        private val _badgeCount = MutableStateFlow(0)
        val badgeCount: StateFlow<Int> = _badgeCount

        private val _hasNews = MutableStateFlow(false)
        val hasNews: StateFlow<Boolean> = _hasNews

        private val _notifications =
            MutableStateFlow(
                listOf(
                    Notification(
                        title = "Joghurt expires tomorrow",
                        id = "1",
                        type = "food_expiring",
                        destinationScreen = "home",
                        description = "Consume it promptly to save waste",
                        buttonTextId = R.string.see_expiring_food,
                        imageResId = R.drawable.warning,
                    ),
                    Notification(
                        title = "Your progress this week",
                        id = "2",
                        type = "statistics",
                        destinationScreen = "statistics",
                        description = "You used 15 foods in time this week",
                        buttonTextId = R.string.see_statistics,
                        imageResId = R.drawable.statistics,
                    ),
                    Notification(
                        title = "Tip of the day",
                        id = "3",
                        type = "tips",
                        destinationScreen = "tips",
                        description =
                            "Store fruit and vegetables separately to keep them fresh for " +
                                "longer",
                        buttonTextId = R.string.view_more_tips,
                        imageResId = R.drawable.tip,
                    ),
                    Notification(
                        title = "Time for an inventory check",
                        id = "4",
                        type = "inventory",
                        destinationScreen = "inventory",
                        description = "It's been 5 days since you last checked your inventory",
                        buttonTextId = R.string.check_inventories,
                        imageResId = R.drawable.reminder,
                    ),
                    Notification(
                        title = "Milk expires in 2 days",
                        id = "5",
                        type = "food_expiring",
                        destinationScreen = "home",
                        description = "Use it soon to avoid spoilage",
                        buttonTextId = R.string.check_expiring_items,
                        imageResId = R.drawable.warning,
                    ),
                    Notification(
                        title = "Emma joined your household group",
                        id = "6",
                        type = "household",
                        destinationScreen = "household",
                        description = "Manage food items together with Emma",
                        buttonTextId = R.string.view_household,
                        imageResId = R.drawable.household,
                    ),
                    Notification(
                        title = "Weekly tip: Organize your fridge",
                        id = "7",
                        type = "tips",
                        destinationScreen = "tips",
                        description = "Place items nearing expiration in front to consume first",
                        buttonTextId = R.string.view_more_tips,
                        imageResId = R.drawable.tip,
                    ),
                    Notification(
                        title = "Your inventory needs attention",
                        id = "8",
                        type = "inventory",
                        destinationScreen = "inventory",
                        description = "You have 3 items expiring soon",
                        buttonTextId = R.string.check_inventories,
                        imageResId = R.drawable.warning,
                    ),
                    Notification(
                        title = "Anna added new items to inventory",
                        id = "9",
                        type = "inventory",
                        destinationScreen = "inventory",
                        description = "See what items Anna added",
                        buttonTextId = R.string.view_items,
                        imageResId = R.drawable.inventory,
                    ),
                    Notification(
                        title = "Your savings this month",
                        id = "10",
                        type = "statistics",
                        destinationScreen = "statistics",
                        description = "You reduced food waste by 20% this month!",
                        buttonTextId = R.string.view_savings,
                        imageResId = R.drawable.statistics,
                    ),
                ),
            )

        val notifications: StateFlow<List<Notification>> = _notifications

        fun removeNotification(notification: Notification) {
            viewModelScope.launch {
                _notifications.value =
                    _notifications.value.toMutableList().apply {
                        remove(notification)
                    }
            }
        }
    }

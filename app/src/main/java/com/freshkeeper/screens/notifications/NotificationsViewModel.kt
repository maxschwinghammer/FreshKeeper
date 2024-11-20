package com.freshkeeper.screens.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freshkeeper.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NotificationsViewModel : ViewModel() {
    private val _badgeCount = MutableStateFlow(0)
    val badgeCount: StateFlow<Int> = _badgeCount

    private val _hasNews = MutableStateFlow(false)
    val hasNews: StateFlow<Boolean> = _hasNews

    private val _notifications =
        MutableStateFlow(
            listOf(
                Notification(
                    title = "Joghurt expires tomorrow",
                    destinationScreen = "home",
                    description = "Consume it promptly to save waste",
                    buttonText = "See expiring food",
                    imageResId = R.drawable.warning,
                ),
                Notification(
                    title = "Max created a household group",
                    destinationScreen = "household",
                    description = "You can now manage your food together",
                    buttonText = "View household",
                    imageResId = R.drawable.household,
                ),
                Notification(
                    title = "Your progress this week",
                    destinationScreen = "statistics",
                    description = "You used 15 foods in time this week",
                    buttonText = "See statistics",
                    imageResId = R.drawable.statistics,
                ),
                Notification(
                    title = "Tip of the day",
                    destinationScreen = "tips",
                    description =
                        "Store fruit and vegetables separately to keep them fresh for " +
                            "longer",
                    buttonText = "View more tips",
                    imageResId = R.drawable.tip,
                ),
                Notification(
                    title = "Time for an inventory check",
                    destinationScreen = "inventory",
                    description = "It's been 5 days since you last checked your inventory",
                    buttonText = "Check inventories",
                    imageResId = R.drawable.reminder,
                ),
                Notification(
                    title = "Milk expires in 2 days",
                    destinationScreen = "home",
                    description = "Use it soon to avoid spoilage",
                    buttonText = "Check expiring items",
                    imageResId = R.drawable.warning,
                ),
                Notification(
                    title = "Emma joined your household group",
                    destinationScreen = "household",
                    description = "Manage food items together with Emma",
                    buttonText = "View household",
                    imageResId = R.drawable.household,
                ),
                Notification(
                    title = "Weekly tip: Organize your fridge",
                    destinationScreen = "tips",
                    description = "Place items nearing expiration in front to consume first",
                    buttonText = "Read more tips",
                    imageResId = R.drawable.tip,
                ),
                Notification(
                    title = "Your inventory needs attention",
                    destinationScreen = "inventory",
                    description = "You have 3 items expiring soon",
                    buttonText = "Check inventory",
                    imageResId = R.drawable.warning,
                ),
                Notification(
                    title = "Anna added new items to inventory",
                    destinationScreen = "inventory",
                    description = "See what items Anna added",
                    buttonText = "View items",
                    imageResId = R.drawable.inventory,
                ),
                Notification(
                    title = "Your savings this month",
                    destinationScreen = "statistics",
                    description = "You reduced food waste by 20% this month!",
                    buttonText = "View savings",
                    imageResId = R.drawable.statistics,
                ),
            ),
        )

    val notifications: StateFlow<List<Notification>> = _notifications

    fun updateBadgeCount(count: Int) {
        viewModelScope.launch {
            _badgeCount.value = count
            _hasNews.value = count > 0
        }
    }

    fun removeNotification(notification: Notification) {
        viewModelScope.launch {
            _notifications.value =
                _notifications.value.toMutableList().apply {
                    remove(notification)
                }
        }
    }
}

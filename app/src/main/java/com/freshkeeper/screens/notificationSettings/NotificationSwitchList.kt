package com.freshkeeper.screens.notificationSettings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.freshkeeper.model.NotificationSettings
import com.freshkeeper.screens.notificationSettings.cards.NotificationSwitchCard
import com.freshkeeper.screens.notificationSettings.viewmodel.NotificationSettingsViewModel
import com.freshkeeper.service.notificationSwitchMap

@Suppress("ktlint:standard:function-naming")
@Composable
fun NotificationSwitchList(
    notificationSettings: NotificationSettings,
    viewModel: NotificationSettingsViewModel,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp),
    ) {
        notificationSwitchMap.forEach { (title, key) ->
            val switchChecked =
                when (key) {
                    "daily_reminders" -> notificationSettings.dailyReminders
                    "food_added" -> notificationSettings.foodAdded
                    "household_changes" -> notificationSettings.householdChanges
                    "food_expiring" -> notificationSettings.foodExpiring
                    "tips" -> notificationSettings.tips
                    "statistics" -> notificationSettings.statistics
                    else -> false
                }

            NotificationSwitchCard(
                title = stringResource(title),
                isChecked = switchChecked,
                onCheckedChange = { newState ->
                    when (key) {
                        "daily_reminders" -> viewModel.updateDailyReminders(newState)
                        "food_added" -> viewModel.updateFoodAdded(newState)
                        "household_changes" -> viewModel.updateHouseholdChanges(newState)
                        "food_expiring" -> viewModel.updateFoodExpiring(newState)
                        "tips" -> viewModel.updateTips(newState)
                        "statistics" -> viewModel.updateStatistics(newState)
                    }
                },
            )
        }
    }
}

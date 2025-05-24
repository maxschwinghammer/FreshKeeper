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
import com.freshkeeper.model.NotificationSwitch
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
                    NotificationSwitch.DAILY_REMINDERS -> notificationSettings.dailyReminders
                    NotificationSwitch.FOOD_ADDED -> notificationSettings.foodAdded
                    NotificationSwitch.HOUSEHOLD_CHANGES -> notificationSettings.householdChanges
                    NotificationSwitch.FOOD_EXPIRING -> notificationSettings.foodExpiring
                    NotificationSwitch.TIPS -> notificationSettings.tips
                    NotificationSwitch.STATISTICS -> notificationSettings.statistics
                }

            NotificationSwitchCard(
                title = stringResource(title),
                isChecked = switchChecked,
                onCheckedChange = { newState ->
                    when (key) {
                        NotificationSwitch.DAILY_REMINDERS -> viewModel.updateDailyReminders(newState)
                        NotificationSwitch.FOOD_ADDED -> viewModel.updateFoodAdded(newState)
                        NotificationSwitch.HOUSEHOLD_CHANGES -> viewModel.updateHouseholdChanges(newState)
                        NotificationSwitch.FOOD_EXPIRING -> viewModel.updateFoodExpiring(newState)
                        NotificationSwitch.TIPS -> viewModel.updateTips(newState)
                        NotificationSwitch.STATISTICS -> viewModel.updateStatistics(newState)
                    }
                },
            )
        }
    }
}

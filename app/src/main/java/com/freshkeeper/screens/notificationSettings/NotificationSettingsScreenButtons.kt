package com.freshkeeper.screens.notificationSettings

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freshkeeper.R
import com.freshkeeper.model.NotificationSettings
import com.freshkeeper.screens.notificationSettings.viewmodel.NotificationSettingsViewModel
import com.freshkeeper.screens.profileSettings.card
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.GreyColor
import com.freshkeeper.ui.theme.LightGreyColor
import java.time.LocalTime

@Suppress("ktlint:standard:function-naming")
@Composable
fun NotificationSwitchList(
    notificationSettings: NotificationSettings,
    viewModel: NotificationSettingsViewModel,
) {
    val switches =
        listOf(
            stringResource(R.string.daily_reminders) to "daily_reminders",
            stringResource(R.string.food_added) to "food_added",
            stringResource(R.string.household_changes) to "household_changes",
            stringResource(R.string.food_expiring) to "food_expiring",
            stringResource(R.string.tips) to "tips",
            stringResource(R.string.statistics) to "statistics",
        )

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp),
    ) {
        switches.forEach { (title, key) ->
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

            NotificationSwitch(
                title = title,
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

@Suppress("ktlint:standard:function-naming")
@Composable
fun NotificationSwitch(
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ComponentBackgroundColor),
        modifier =
            Modifier
                .fillMaxWidth()
                .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp)),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                color = Color.White,
                fontSize = 16.sp,
            )
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors =
                    SwitchDefaults.colors(
                        checkedBorderColor = ComponentStrokeColor,
                        checkedTrackColor = GreyColor,
                        checkedThumbColor = AccentTurquoiseColor,
                        uncheckedBorderColor = ComponentStrokeColor,
                        uncheckedTrackColor = GreyColor,
                        uncheckedThumbColor = LightGreyColor,
                    ),
                modifier = Modifier.scale(0.9f),
            )
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun NotificationPermissionButton(
    context: Context,
    isNotificationEnabled: Boolean,
) {
    Card(
        modifier =
            Modifier.card().border(
                1.dp,
                ComponentStrokeColor,
                RoundedCornerShape(10.dp),
            ),
        onClick = {
            val intent =
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(
                        Settings.EXTRA_APP_PACKAGE,
                        context.packageName,
                    )
                }
            context.startActivity(intent)
        },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .background(ComponentBackgroundColor)
                    .fillMaxWidth()
                    .padding(16.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                if (isNotificationEnabled) {
                    Text(text = stringResource(R.string.revoke_notification_permissions))
                } else {
                    Text(text = stringResource(R.string.grant_notification_permissions))
                }
            }
            Image(
                painter = painterResource(R.drawable.notification),
                contentDescription = "Icon",
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun SelectDailyNotificationTime(
    selectedTime: LocalTime,
    onTimeSelected: (LocalTime) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        TimePickerDialog(
            selectedTime = selectedTime,
            onDismiss = { showDialog = false },
            onTimeSelected = {
                onTimeSelected(it)
                showDialog = false
            },
        )
    }

    Card(
        modifier =
            Modifier.card().border(
                1.dp,
                ComponentStrokeColor,
                RoundedCornerShape(10.dp),
            ),
        onClick = { showDialog = true },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .background(ComponentBackgroundColor)
                    .fillMaxWidth()
                    .padding(16.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = stringResource(R.string.select_daily_notification_time))
            }
            Image(
                painter = painterResource(R.drawable.time),
                contentDescription = "Time Icon",
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun UpdateTimeBeforeExpiration(
    timeBeforeExpiration: Int,
    onTimeSelected: (Int) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedTime by remember { mutableIntStateOf(timeBeforeExpiration) }

    Card(
        modifier =
            Modifier.card().border(
                1.dp,
                ComponentStrokeColor,
                RoundedCornerShape(10.dp),
            ),
        onClick = { showDialog = true },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .background(ComponentBackgroundColor)
                    .fillMaxWidth()
                    .padding(16.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = stringResource(R.string.select_time_before_expiration))
            }
            Image(
                painter = painterResource(R.drawable.expiring_soon),
                contentDescription = "Time Icon",
                modifier = Modifier.size(24.dp),
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            containerColor = ComponentBackgroundColor,
            onDismissRequest = { showDialog = false },
            title = { Text(text = stringResource(R.string.select_time_before_expiration)) },
            text = {
                LazyColumn(
                    modifier = Modifier.height(200.dp),
                ) {
                    items(30) { index ->
                        val time = index + 1
                        val borderColor =
                            if (time == selectedTime) {
                                AccentTurquoiseColor
                            } else {
                                ComponentStrokeColor
                            }
                        Text(
                            text = "$time Tage",
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp)
                                    .clickable {
                                        selectedTime = time
                                        onTimeSelected(time)
                                        showDialog = false
                                    }.border(1.dp, borderColor, RoundedCornerShape(10.dp))
                                    .padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}

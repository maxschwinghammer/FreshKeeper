package com.freshkeeper.screens.notificationSettings

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.freshkeeper.R
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.GreyColor
import com.freshkeeper.ui.theme.TextColor
import java.time.LocalTime

@Suppress("ktlint:standard:function-naming")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    selectedTime: LocalTime,
    onDismiss: () -> Unit,
    onTimeSelected: (LocalTime) -> Unit,
) {
    var hour by remember { mutableIntStateOf(selectedTime.hour) }
    var minute by remember { mutableIntStateOf(selectedTime.minute) }

    val timePickerState =
        rememberTimePickerState(
            initialHour = hour,
            initialMinute = minute,
        )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ComponentBackgroundColor,
        confirmButton = {
            TextButton(onClick = {
                onTimeSelected(LocalTime.of(hour, minute))
                onDismiss()
            }) {
                Text("OK", color = AccentTurquoiseColor)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.cancel), color = AccentTurquoiseColor)
            }
        },
        text = {
            TimePicker(
                state = timePickerState,
                colors =
                    TimePickerDefaults.colors(
                        clockDialColor = GreyColor,
                        clockDialUnselectedContentColor = TextColor,
                        selectorColor = AccentTurquoiseColor,
                        containerColor = ComponentBackgroundColor,
                        periodSelectorBorderColor = GreyColor.copy(alpha = 0.5f),
                        periodSelectorSelectedContainerColor = AccentTurquoiseColor.copy(alpha = 0.2f),
                        periodSelectorUnselectedContainerColor = ComponentBackgroundColor.copy(alpha = 0.1f),
                        periodSelectorSelectedContentColor = AccentTurquoiseColor,
                        periodSelectorUnselectedContentColor = TextColor.copy(alpha = 0.6f),
                        timeSelectorSelectedContainerColor = AccentTurquoiseColor.copy(alpha = 0.3f),
                        timeSelectorUnselectedContainerColor = ComponentBackgroundColor.copy(alpha = 0.1f),
                        timeSelectorSelectedContentColor = AccentTurquoiseColor,
                        timeSelectorUnselectedContentColor = TextColor.copy(alpha = 0.5f),
                    ),
            )
        },
    )
    hour = timePickerState.hour
    minute = timePickerState.minute
}

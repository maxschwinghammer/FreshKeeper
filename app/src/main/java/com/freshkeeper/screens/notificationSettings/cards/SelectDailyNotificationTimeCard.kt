package com.freshkeeper.screens.notificationSettings.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.freshkeeper.R
import com.freshkeeper.screens.notificationSettings.TimePickerDialog
import com.freshkeeper.screens.profileSettings.cards.card
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import java.time.LocalTime

@Suppress("ktlint:standard:function-naming")
@Composable
fun SelectDailyNotificationTimeCard(
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
                Text(
                    text = stringResource(R.string.select_daily_notification_time),
                    maxLines = 2,
                    overflow = TextOverflow.Clip,
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            Image(
                painter = painterResource(R.drawable.time),
                contentDescription = "Time Icon",
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

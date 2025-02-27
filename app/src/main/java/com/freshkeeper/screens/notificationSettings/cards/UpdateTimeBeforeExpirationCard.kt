package com.freshkeeper.screens.notificationSettings.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.freshkeeper.R
import com.freshkeeper.screens.profileSettings.cards.card
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun UpdateTimeBeforeExpirationCard(
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
                            text = "$time " + stringResource(R.string.days),
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
                TextButton(onClick = {
                    onTimeSelected(timeBeforeExpiration)
                    showDialog = false
                }) {
                    Text(text = stringResource(R.string.cancel), color = AccentTurquoiseColor)
                }
            },
            onDismissRequest = { showDialog = false },
        )
    }
}

package com.freshkeeper.screens.home.service

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.freshkeeper.ui.theme.AccentGreenColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.GreyColor
import com.freshkeeper.ui.theme.TextColor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("ktlint:standard:function-naming")
@Composable
fun ExpiryDatePicker(
    expiryDate: String,
    modifier: Modifier = Modifier,
) {
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var showModal by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = selectedDate?.let { convertMillisToDate(it) } ?: expiryDate,
        onValueChange = { },
        label = { Text("Expiry date") },
        placeholder = { Text("DD.MM.YYYY") },
        trailingIcon = {
            Icon(Icons.Default.DateRange, contentDescription = "Select date")
        },
        modifier =
            modifier
                .fillMaxWidth()
                .pointerInput(selectedDate) {
                    awaitEachGesture {
                        awaitFirstDown(pass = PointerEventPass.Initial)
                        val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                        if (upEvent != null) {
                            showModal = true
                        }
                    }
                },
        colors =
            OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = ComponentStrokeColor,
                focusedBorderColor = AccentGreenColor,
                unfocusedLabelColor = TextColor,
                focusedLabelColor = AccentGreenColor,
            ),
    )

    if (showModal) {
        DatePickerModal(
            onDateSelected = { selectedDate = it },
            onDismiss = { showModal = false },
        )
    }
}

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

@Suppress("ktlint:standard:function-naming")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit,
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        modifier = Modifier.size(400.dp, 550.dp),
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK", color = AccentGreenColor)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = AccentGreenColor)
            }
        },
        colors = DatePickerDefaults.colors(containerColor = GreyColor),
    ) {
        DatePicker(
            state = datePickerState,
            modifier = Modifier.size(400.dp, 500.dp),
            colors =
                DatePickerDefaults.colors(
                    containerColor = GreyColor,
                    currentYearContentColor = AccentGreenColor,
                    dividerColor = ComponentStrokeColor,
                    headlineContentColor = TextColor,
                    navigationContentColor = TextColor,
                    selectedDayContainerColor = AccentGreenColor,
                    selectedDayContentColor = GreyColor,
                    selectedYearContainerColor = AccentGreenColor,
                    selectedYearContentColor = GreyColor,
                    subheadContentColor = TextColor,
                    titleContentColor = TextColor,
                    todayContentColor = AccentGreenColor,
                    todayDateBorderColor = AccentGreenColor,
                    weekdayContentColor = TextColor,
                    yearContentColor = TextColor,
                ),
        )
    }
}

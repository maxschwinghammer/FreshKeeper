package com.freshkeeper.screens.home

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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.freshkeeper.R
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.GreyColor
import com.freshkeeper.ui.theme.TextColor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("ktlint:standard:function-naming")
@Composable
fun ExpiryDatePicker(
    expiryDate: Long?,
    onDateChange: (Long?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedDate by remember {
        mutableLongStateOf(if ((expiryDate ?: 0L) <= 0L) System.currentTimeMillis() else expiryDate!!)
    }
    var showModal by remember { mutableStateOf(false) }
    val currentDate = System.currentTimeMillis()

    OutlinedTextField(
        value = convertMillisToDate(selectedDate),
        onValueChange = { },
        label = { Text(stringResource(R.string.expiry_date)) },
        placeholder = { Text(convertMillisToDate(currentDate)) },
        trailingIcon = {
            Icon(Icons.Default.DateRange, contentDescription = stringResource(R.string.select_date))
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
                focusedBorderColor = AccentTurquoiseColor,
                unfocusedLabelColor = TextColor,
                focusedLabelColor = AccentTurquoiseColor,
            ),
    )

    if (showModal) {
        DatePickerModal(
            initialDate = selectedDate,
            onDateSelected = { date ->
                if (date != null) {
                    selectedDate = date
                }
                onDateChange(date)
            },
            onDismiss = { showModal = false },
        )
    }
}

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    formatter.timeZone = java.util.TimeZone.getDefault()
    return formatter.format(Date(millis))
}

@Suppress("ktlint:standard:function-naming")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    initialDate: Long?,
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit,
) {
    val datePickerState =
        rememberDatePickerState(
            initialSelectedDateMillis = initialDate ?: System.currentTimeMillis(),
        )

    DatePickerDialog(
        modifier = Modifier.size(400.dp, 550.dp),
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val selectedDate = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                onDateSelected(selectedDate)
                onDismiss()
            }) {
                Text("OK", color = AccentTurquoiseColor)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel), color = AccentTurquoiseColor)
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
                    currentYearContentColor = AccentTurquoiseColor,
                    dividerColor = ComponentStrokeColor,
                    headlineContentColor = TextColor,
                    navigationContentColor = TextColor,
                    selectedDayContainerColor = AccentTurquoiseColor,
                    selectedDayContentColor = GreyColor,
                    selectedYearContainerColor = AccentTurquoiseColor,
                    selectedYearContentColor = GreyColor,
                    subheadContentColor = TextColor,
                    titleContentColor = TextColor,
                    todayContentColor = AccentTurquoiseColor,
                    todayDateBorderColor = AccentTurquoiseColor,
                    weekdayContentColor = TextColor,
                    yearContentColor = TextColor,
                ),
        )
    }
}

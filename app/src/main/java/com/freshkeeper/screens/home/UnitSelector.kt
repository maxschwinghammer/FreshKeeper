package com.freshkeeper.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.freshkeeper.R
import com.freshkeeper.ui.theme.AccentGreenColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.GreyColor
import com.freshkeeper.ui.theme.TextColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun UnitSelector(unit: MutableState<String>) {
    val units =
        listOf(
            stringResource(R.string.kg),
            stringResource(R.string.g),
            stringResource(R.string.l),
            stringResource(R.string.ml),
            stringResource(R.string.pcs),
            stringResource(R.string.pckg),
            stringResource(R.string.can),
            stringResource(R.string.box),
            stringResource(R.string.tbsp),
            stringResource(R.string.tsp),
            stringResource(R.string.cup),
            stringResource(R.string.sachet),
            stringResource(R.string.bag),
        )
    val label = stringResource(R.string.unit)
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    var expanded by remember { mutableStateOf(false) }

    val icon =
        if (expanded) {
            Icons.Filled.KeyboardArrowUp
        } else {
            Icons.Filled.KeyboardArrowDown
        }

    Column(modifier = Modifier.padding(start = 10.dp)) {
        OutlinedTextField(
            value = unit.value,
            onValueChange = { unit.value = it },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        textFieldSize = coordinates.size.toSize()
                    },
            label = { Text(label, color = TextColor) },
            trailingIcon = {
                Icon(
                    icon,
                    "contentDescription",
                    Modifier.clickable { expanded = !expanded },
                )
            },
            readOnly = true,
            colors =
                OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = ComponentStrokeColor,
                    focusedBorderColor = AccentGreenColor,
                    unfocusedLabelColor = TextColor,
                    focusedLabelColor = AccentGreenColor,
                    focusedTextColor = TextColor,
                    unfocusedTextColor = TextColor,
                ),
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier =
                Modifier
                    .width(100.dp)
                    .background(GreyColor)
                    .clip(RoundedCornerShape(10.dp)),
        ) {
            units.forEach { label ->
                DropdownMenuItem(
                    text = { Text(label, color = TextColor) },
                    onClick = {
                        unit.value = label
                        expanded = false
                    },
                )
            }
        }
    }
}

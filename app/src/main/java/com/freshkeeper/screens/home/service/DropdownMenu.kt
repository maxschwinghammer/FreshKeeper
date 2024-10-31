package com.freshkeeper.screens.home.service

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.freshkeeper.R
import com.freshkeeper.ui.theme.AccentGreenColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.Grey
import com.freshkeeper.ui.theme.TextColor
import java.util.Locale

@Suppress("ktlint:standard:function-naming")
@Composable
fun DropdownMenu(
    options: List<String>,
    label: String,
) {
    var selectedText by
        when (label) {
            "Category" -> {
                remember { mutableStateOf("Dairy goods") }
            }
            else -> {
                remember { mutableStateOf("Fridge") }
            }
        }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    var expanded by remember { mutableStateOf(false) }

    val icon =
        if (expanded) {
            Icons.Filled.KeyboardArrowUp
        } else {
            Icons.Filled.KeyboardArrowDown
        }

    Column {
        OutlinedTextField(
            value = selectedText,
            onValueChange = { selectedText = it },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        textFieldSize = coordinates.size.toSize()
                    },
            label = { Text(label, color = TextColor) },
            leadingIcon = { LeadingIcon(selectedText) },
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
                    .width(200.dp)
                    .background(Grey)
                    .clip(RoundedCornerShape(10.dp)),
        ) {
            options.forEach { label ->
                val iconName = label.lowercase(Locale.ROOT).replace(" ", "_")
                val iconResId =
                    try {
                        R.drawable::class.java.getDeclaredField(iconName).getInt(null)
                    } catch (e: Exception) {
                        null
                    }

                DropdownMenuItem(
                    text = { Text(label, color = TextColor) },
                    leadingIcon = {
                        iconResId?.let {
                            Image(
                                painter = painterResource(id = it),
                                contentDescription = label,
                                modifier = Modifier.size(25.dp),
                            )
                        }
                    },
                    onClick = {
                        selectedText = label
                        expanded = false
                    },
                )
            }
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun LeadingIcon(selectedText: String) {
    val iconName = selectedText.lowercase(Locale.ROOT).replace(" ", "_")
    val iconResId =
        try {
            R.drawable::class.java.getDeclaredField(iconName).getInt(null)
        } catch (e: Exception) {
            null
        }

    iconResId?.let {
        Image(
            painter = painterResource(id = it),
            contentDescription = selectedText,
            modifier = Modifier.size(25.dp),
        )
    }
}

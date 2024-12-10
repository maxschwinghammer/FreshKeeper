package com.freshkeeper.screens.home

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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.freshkeeper.R
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.GreyColor
import com.freshkeeper.ui.theme.TextColor
import java.util.Locale

@Suppress("ktlint:standard:function-naming")
@Composable
fun DropdownMenu(
    selectedId: Int,
    onSelect: (Int) -> Unit,
    type: String,
    label: String,
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedIdState by remember { mutableIntStateOf(selectedId) }
    val selected = stringResource(id = selectedIdState)

    val storageLocations =
        listOf(
            R.string.fridge,
            R.string.cupboard,
            R.string.freezer,
            R.string.counter_top,
            R.string.cellar,
            R.string.bread_box,
            R.string.spice_rack,
            R.string.pantry,
            R.string.fruit_basket,
            R.string.other,
        )
    val categories =
        listOf(
            R.string.dairy_goods,
            R.string.vegetables,
            R.string.fruits,
            R.string.meat,
            R.string.fish,
            R.string.frozen_goods,
            R.string.spices,
            R.string.bread,
            R.string.confectionery,
            R.string.drinks,
            R.string.noodles,
            R.string.canned_goods,
            R.string.candy,
            R.string.other,
        )

    val options =
        when (type) {
            "storageLocations" -> storageLocations
            "categories" -> categories
            else -> emptyList()
        }

    val icon =
        if (expanded) {
            Icons.Filled.KeyboardArrowUp
        } else {
            Icons.Filled.KeyboardArrowDown
        }

    Column {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            modifier =
                Modifier
                    .fillMaxWidth(),
            label = { Text(label, color = TextColor) },
            leadingIcon = { LeadingIcon(selected) },
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
                    focusedBorderColor = AccentTurquoiseColor,
                    unfocusedLabelColor = TextColor,
                    focusedLabelColor = AccentTurquoiseColor,
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
                    .background(GreyColor)
                    .clip(RoundedCornerShape(10.dp)),
        ) {
            options.forEach { option ->
                val optionString = stringResource(id = option)
                val iconName = optionString.lowercase(Locale.ROOT).replace(" ", "_")
                val iconResId =
                    try {
                        R.drawable::class.java.getDeclaredField(iconName).getInt(null)
                    } catch (e: Exception) {
                        null
                    }

                DropdownMenuItem(
                    text = { Text(optionString, color = TextColor) },
                    leadingIcon = {
                        iconResId?.let {
                            Image(
                                painter = painterResource(id = it),
                                contentDescription = optionString,
                                modifier = Modifier.size(25.dp),
                            )
                        }
                    },
                    onClick = {
                        selectedIdState = option
                        onSelect(option)
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

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
    onSelect: (String) -> Unit,
    type: String,
    label: String,
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = stringResource(id = selectedId)

    val storageLocations =
        listOf(
            "Fridge",
            "Cupboard",
            "Freezer",
            "Counter top",
            "Cellar",
            "Bread box",
            "Spice rack",
            "Pantry",
            "Fruit basket",
            "Other",
        )
    val categories =
        listOf(
            "Dairy goods",
            "Vegetables",
            "Fruits",
            "Meat",
            "Fish",
            "Frozen Goods",
            "Spices",
            "Bread",
            "Confectionery",
            "Drinks",
            "Noodles",
            "Canned goods",
            "Candy",
            "Other",
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
                val translatedLabel = stringResource(id = getCategoryStringRes(option))
                val iconName = option.lowercase(Locale.ROOT).replace(" ", "_")
                val iconResId =
                    try {
                        R.drawable::class.java.getDeclaredField(iconName).getInt(null)
                    } catch (e: Exception) {
                        null
                    }

                DropdownMenuItem(
                    text = { Text(translatedLabel, color = TextColor) },
                    leadingIcon = {
                        iconResId?.let {
                            Image(
                                painter = painterResource(id = it),
                                contentDescription = option,
                                modifier = Modifier.size(25.dp),
                            )
                        }
                    },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    },
                )
            }
        }
    }
}

fun getCategoryStringRes(category: String): Int =
    when (category) {
        "Dairy goods" -> R.string.dairy_goods
        "Vegetables" -> R.string.vegetables
        "Fruits" -> R.string.fruits
        "Meat" -> R.string.meat
        "Fish" -> R.string.fish
        "Frozen Goods" -> R.string.frozen_goods
        "Spices" -> R.string.spices
        "Bread" -> R.string.bread
        "Confectionery" -> R.string.confectionery
        "Drinks" -> R.string.drinks
        "Noodles" -> R.string.noodles
        "Canned goods" -> R.string.canned_goods
        "Candy" -> R.string.candy
        "Fridge" -> R.string.fridge
        "Cupboard" -> R.string.cupboard
        "Freezer" -> R.string.freezer
        "Counter top" -> R.string.counter_top
        "Cellar" -> R.string.cellar
        "Bread box" -> R.string.bread_box
        "Spice rack" -> R.string.spice_rack
        "Pantry" -> R.string.pantry
        "Fruit basket" -> R.string.fruit_basket
        "Other" -> R.string.other
        else -> R.string.other
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

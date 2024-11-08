package com.freshkeeper.sheets

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.freshkeeper.screens.home.service.DropdownMenu
import com.freshkeeper.screens.home.service.ExpiryDatePicker
import com.freshkeeper.screens.home.service.UnitSelector
import com.freshkeeper.ui.theme.AccentGreenColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.TextColor
import com.freshkeeper.ui.theme.WhiteColor

@Suppress("ktlint:standard:function-naming")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductSheet(
    sheetState: SheetState,
    expiryDateValue: String,
) {
    var productName by remember { mutableStateOf("") }
    val expiryDate by remember { mutableStateOf(expiryDateValue) }
    var quantity by remember { mutableStateOf("") }
    val unit = remember { mutableStateOf("") }
    val imageUrl by remember { mutableStateOf("") }
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
    var isConsumedChecked by remember { mutableStateOf(false) }
    var isThrownAwayChecked by remember { mutableStateOf(false) }

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Edit product",
            color = TextColor,
            fontSize = 18.sp,
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = productName,
                    onValueChange = { productName = it },
                    label = { Text("Product name") },
                    colors =
                        OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = ComponentStrokeColor,
                            focusedBorderColor = AccentGreenColor,
                            unfocusedLabelColor = TextColor,
                            focusedLabelColor = AccentGreenColor,
                        ),
                    maxLines = 1,
                )

                Spacer(modifier = Modifier.height(8.dp))

                ExpiryDatePicker(expiryDate)
            }

            if (imageUrl.isNotEmpty()) {
                Box(
                    modifier =
                        Modifier
                            .padding(top = 8.dp, start = 16.dp)
                            .defaultMinSize(minWidth = 150.dp, minHeight = 129.dp)
                            .weight(1f)
                            .heightIn(max = 129.dp),
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUrl),
                        contentDescription = "Product Image",
                        contentScale = ContentScale.Fit,
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp)),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = quantity,
                onValueChange = { if (it.matches(Regex("\\d{0,4}"))) quantity = it },
                label = { Text("Quantity") },
                modifier = Modifier.weight(1f),
                colors =
                    OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = ComponentStrokeColor,
                        focusedBorderColor = AccentGreenColor,
                        unfocusedLabelColor = TextColor,
                        focusedLabelColor = AccentGreenColor,
                    ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                maxLines = 1,
            )

            Box(modifier = Modifier.weight(1f).padding(start = 6.dp)) {
                UnitSelector(unit = unit)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        DropdownMenu(storageLocations, "Storage location")

        Spacer(modifier = Modifier.height(8.dp))

        DropdownMenu(categories, "Category")

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = isConsumedChecked,
                onCheckedChange = {
                    isConsumedChecked = it
                    if (it) isThrownAwayChecked = false
                },
                enabled = !isThrownAwayChecked,
                colors =
                    CheckboxDefaults.colors(
                        checkmarkColor = ComponentBackgroundColor,
                        checkedColor = AccentGreenColor,
                    ),
            )
            Text(
                "Mark as consumed",
                color = TextColor,
                modifier = Modifier.padding(start = 8.dp),
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = isThrownAwayChecked,
                onCheckedChange = {
                    isThrownAwayChecked = it
                    if (it) isConsumedChecked = false
                },
                enabled = !isConsumedChecked,
                colors =
                    CheckboxDefaults.colors(
                        checkmarkColor = ComponentBackgroundColor,
                        checkedColor = AccentGreenColor,
                    ),
            )
            Text(
                "Mark as thrown away",
                color = TextColor,
                modifier = Modifier.padding(start = 8.dp),
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                // Handle submit action here
            },
            modifier =
                Modifier
                    .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = WhiteColor),
        ) {
            Text("Save changes", color = ComponentBackgroundColor)
        }
    }
}

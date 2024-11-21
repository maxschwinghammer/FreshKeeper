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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.freshkeeper.R
import com.freshkeeper.screens.home.DropdownMenu
import com.freshkeeper.screens.home.ExpiryDatePicker
import com.freshkeeper.screens.home.UnitSelector
import com.freshkeeper.screens.home.fetchProductDataFromBarcode
import com.freshkeeper.ui.theme.AccentGreenColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.TextColor
import com.freshkeeper.ui.theme.WhiteColor

@Suppress("ktlint:standard:function-naming")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualInputSheet(
    sheetState: SheetState,
    barcodeValue: String,
    expiryDateValue: String,
) {
    var productName by remember { mutableStateOf("") }
    val expiryDate by remember { mutableStateOf(expiryDateValue) }
    var quantity by remember { mutableStateOf("") }
    val unit = remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
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

    LaunchedEffect(barcodeValue) {
        val productData = fetchProductDataFromBarcode(barcodeValue)
        productName = productData?.name ?: barcodeValue
        quantity = productData?.quantity ?: ""
        unit.value = productData?.unit ?: ""
        imageUrl = productData?.imageUrl ?: ""
    }

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.new_entry),
            fontSize = 18.sp,
            color = TextColor,
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = productName,
                    onValueChange = { productName = it },
                    label = { Text(stringResource(R.string.product_name)) },
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
                label = { Text(stringResource(R.string.quantity)) },
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

        DropdownMenu(storageLocations, stringResource(R.string.storage_location))

        Spacer(modifier = Modifier.height(8.dp))

        DropdownMenu(categories, stringResource(R.string.category))

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Handle submit action here
            },
            modifier =
                Modifier
                    .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = WhiteColor),
        ) {
            Text(stringResource(R.string.add_product), color = ComponentBackgroundColor)
        }
    }
}

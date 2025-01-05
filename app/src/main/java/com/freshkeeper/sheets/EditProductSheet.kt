package com.freshkeeper.sheets

import android.util.Log
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.freshkeeper.screens.home.viewmodel.FoodItem
import com.freshkeeper.ui.theme.AccentGreenColor
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.RedColor
import com.freshkeeper.ui.theme.TextColor
import com.freshkeeper.ui.theme.WhiteColor
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("ktlint:standard:function-naming")
@Composable
fun EditProductSheet(
    sheetState: SheetState,
    foodItem: FoodItem,
) {
    var productName by remember { mutableStateOf(foodItem.name) }
    var quantity by remember { mutableStateOf(foodItem.quantity.toString()) }
    val unit = remember { mutableStateOf(foodItem.unit) }
    val storageLocation = remember { mutableIntStateOf(foodItem.storageLocation) }
    val category = remember { mutableIntStateOf(foodItem.category) }
    var isConsumedChecked by remember { mutableStateOf(foodItem.consumed) }
    var isThrownAwayChecked by remember { mutableStateOf(foodItem.thrownAway) }
    val imageUrl by remember { mutableStateOf(foodItem.imageUrl) }

    var selectedCategory by remember { mutableIntStateOf(R.string.meat) }
    var selectedStorageLocation by remember { mutableIntStateOf(R.string.fridge) }

    var expiryDate by remember { mutableLongStateOf(foodItem.expiryTimestamp) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.edit_product),
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
                    label = { Text(stringResource(R.string.product_name)) },
                    colors =
                        OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = ComponentStrokeColor,
                            focusedBorderColor = AccentTurquoiseColor,
                            unfocusedLabelColor = TextColor,
                            focusedLabelColor = AccentTurquoiseColor,
                        ),
                    maxLines = 1,
                )

                Spacer(modifier = Modifier.height(8.dp))

                ExpiryDatePicker(
                    expiryDate = expiryDate,
                    onDateChange = { newDate ->
                        if (newDate != null) {
                            expiryDate = newDate
                        }
                    },
                )
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
                        focusedBorderColor = AccentTurquoiseColor,
                        unfocusedLabelColor = TextColor,
                        focusedLabelColor = AccentTurquoiseColor,
                    ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                maxLines = 1,
            )

            Box(modifier = Modifier.weight(1f).padding(start = 6.dp)) {
                UnitSelector(unit = unit)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        DropdownMenu(
            storageLocation.intValue,
            onSelect = { selectedStorageLocation = it },
            "storageLocations",
            stringResource(R.string.storage_location),
        )

        Spacer(modifier = Modifier.height(8.dp))

        DropdownMenu(
            category.intValue,
            onSelect = { selectedCategory = it },
            "categories",
            stringResource(R.string.category),
        )

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
                stringResource(R.string.mark_as_consumed),
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
                        checkedColor = RedColor,
                    ),
            )
            Text(
                stringResource(R.string.mark_as_thrown_away),
                color = TextColor,
                modifier = Modifier.padding(start = 8.dp),
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val firestore = FirebaseFirestore.getInstance()

                firestore
                    .collection("foodItems")
                    .whereEqualTo("id", foodItem.id)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        val document = querySnapshot.documents.firstOrNull()
                        if (document != null) {
                            document.reference
                                .update(
                                    mapOf(
                                        "name" to productName,
                                        "quantity" to quantity.toInt(),
                                        "unit" to unit.value,
                                        "storageLocation" to selectedStorageLocation,
                                        "category" to selectedCategory,
                                        "expiryTimestamp" to expiryDate,
                                        "consumed" to isConsumedChecked,
                                        "thrownAway" to isThrownAwayChecked,
                                    ),
                                ).addOnSuccessListener {
                                    Log.d("Firestore", "Product updated successfully")
                                    coroutineScope.launch {
                                        sheetState.hide()
                                    }
                                }.addOnFailureListener { e ->
                                    Log.w("Firestore", "Error when updating the product", e)
                                }
                        } else {
                            Log.w("Firestore", "No document found with the given ID")
                        }
                    }.addOnFailureListener { e ->
                        Log.w("Firestore", "Error retrieving document", e)
                    }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = WhiteColor),
        ) {
            Text(stringResource(R.string.save_changes), color = ComponentBackgroundColor)
        }
    }
}

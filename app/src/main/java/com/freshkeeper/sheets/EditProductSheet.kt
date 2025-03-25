package com.freshkeeper.sheets

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.freshkeeper.R
import com.freshkeeper.model.FoodItem
import com.freshkeeper.model.FoodItemPicture
import com.freshkeeper.screens.home.DropdownMenu
import com.freshkeeper.screens.home.ExpiryDatePicker
import com.freshkeeper.screens.home.UnitSelector
import com.freshkeeper.screens.profileSettings.convertBase64ToBitmap
import com.freshkeeper.service.account.AccountServiceImpl
import com.freshkeeper.service.categoryMap
import com.freshkeeper.service.categoryReverseMap
import com.freshkeeper.service.categoryTips
import com.freshkeeper.service.product.ProductServiceImpl
import com.freshkeeper.service.storageLocationMap
import com.freshkeeper.service.storageLocationReverseMap
import com.freshkeeper.ui.theme.AccentGreenColor
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.ExpiredColor
import com.freshkeeper.ui.theme.RedColor
import com.freshkeeper.ui.theme.TextColor
import com.freshkeeper.ui.theme.WhiteColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("ktlint:standard:function-naming")
@Composable
fun EditProductSheet(
    sheetState: SheetState,
    productInfoSheetState: SheetState,
    foodItem: FoodItem,
) {
    val accountService = remember { AccountServiceImpl() }
    val productService = remember { ProductServiceImpl(accountService) }

    var productName by remember { mutableStateOf(foodItem.name) }
    var quantity by remember { mutableStateOf(foodItem.quantity.toString()) }
    val unit = remember { mutableStateOf(foodItem.unit) }
    val storageLocation = remember { mutableStateOf(foodItem.storageLocation) }
    val category = remember { mutableStateOf(foodItem.category) }
    var isConsumedChecked by remember { mutableStateOf(foodItem.consumed) }
    var isThrownAwayChecked by remember { mutableStateOf(foodItem.thrownAway) }

    val selectedStorageLocation = storageLocationMap[storageLocation.value] ?: R.string.fridge
    val selectedCategory = categoryMap[category.value] ?: R.string.dairy_goods

    var expiryDate by remember { mutableLongStateOf(foodItem.expiryTimestamp) }
    val coroutineScope = rememberCoroutineScope()

    var foodItemPicture by remember { mutableStateOf<FoodItemPicture?>(null) }

    LaunchedEffect(foodItem.imageId) {
        foodItem.imageId?.let {
            productService.getFoodItemPicture(it, { picture ->
                foodItemPicture = picture
            }, { e ->
                Log.e("ProductService", "Error fetching food item picture", e)
            })
        }
    }

    val addedText = stringResource(R.string.added_product)

    ModalBottomSheet(
        onDismissRequest = { coroutineScope.launch { sheetState.hide() } },
        sheetState = sheetState,
        containerColor = ComponentBackgroundColor,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (!foodItem.barcode.isNullOrEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.edit_product),
                        color = TextColor,
                        fontSize = 18.sp,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f).padding(start = 30.dp),
                        textAlign = TextAlign.Center,
                    )
                    Image(
                        painter = painterResource(R.drawable.info),
                        contentDescription = "Info",
                        modifier =
                            Modifier
                                .padding(end = 10.dp)
                                .size(20.dp)
                                .clickable {
                                    coroutineScope.launch {
                                        productInfoSheetState.show()
                                        sheetState.hide()
                                    }
                                },
                    )
                }
            } else {
                Text(
                    text = stringResource(R.string.edit_product),
                    color = TextColor,
                    fontSize = 18.sp,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                )
            }

            if (foodItem.expiryTimestamp < System.currentTimeMillis()) {
                Spacer(modifier = Modifier.height(16.dp))
                val expiryTime = foodItem.expiryTimestamp
                val currentTime = System.currentTimeMillis()
                val timeDiff = currentTime - expiryTime
                val days = (timeDiff / (1000 * 60 * 60 * 24)).toInt()

                val timeText =
                    when {
                        days < 7 ->
                            stringResource(
                                R.string.expired_since,
                                "$days " +
                                    stringResource(
                                        if (days == 1) {
                                            R.string.day
                                        } else {
                                            R.string.days
                                        },
                                    ),
                            )
                        days < 30 -> {
                            val weeks = days / 7
                            stringResource(
                                R.string.expired_since,
                                "$weeks " +
                                    stringResource(
                                        if (weeks ==
                                            1
                                        ) {
                                            R.string.week
                                        } else {
                                            R.string.weeks
                                        },
                                    ),
                            )
                        }
                        else -> {
                            val months = days / 30
                            stringResource(
                                R.string.expired_since,
                                "$months " +
                                    stringResource(
                                        if (months == 1) {
                                            R.string.month
                                        } else {
                                            R.string.months
                                        },
                                    ),
                            )
                        }
                    }
                Text(
                    text = timeText,
                    color = ExpiredColor,
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                )
                categoryTips[foodItem.category]?.let { tipResId ->
                    Text(
                        text = stringResource(tipResId),
                        color = AccentTurquoiseColor,
                        fontSize = 14.sp,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                    )
                }
            }

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

                if (foodItemPicture != null) {
                    if (foodItemPicture!!.type == "base64") {
                        val decodedImage =
                            foodItemPicture!!.image?.let { image ->
                                convertBase64ToBitmap(image)
                            }
                        if (decodedImage != null) {
                            Box(
                                modifier =
                                    Modifier
                                        .padding(top = 8.dp, start = 16.dp)
                                        .defaultMinSize(minWidth = 150.dp, minHeight = 129.dp)
                                        .weight(1f)
                                        .heightIn(max = 129.dp),
                            ) {
                                Image(
                                    bitmap = decodedImage.asImageBitmap(),
                                    contentDescription = "Product Image",
                                    contentScale = ContentScale.Fit,
                                    modifier =
                                        Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(10.dp))
                                            .border(
                                                1.dp,
                                                ComponentStrokeColor,
                                                RoundedCornerShape(10.dp),
                                            ),
                                )
                            }
                        }
                    } else if (foodItemPicture!!.type == "url") {
                        Box(
                            modifier =
                                Modifier
                                    .padding(top = 8.dp, start = 16.dp)
                                    .defaultMinSize(minWidth = 150.dp, minHeight = 129.dp)
                                    .weight(1f)
                                    .heightIn(max = 129.dp),
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(foodItemPicture!!.image),
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
                selectedStorageLocation,
                onSelect = { selectedStorageLocation ->
                    storageLocation.value = storageLocationReverseMap[selectedStorageLocation]
                        ?: "fridge"
                },
                "storageLocations",
                stringResource(R.string.storage_location),
            )

            Spacer(modifier = Modifier.height(8.dp))

            DropdownMenu(
                selectedCategory,
                onSelect = { selectedCategory ->
                    category.value = categoryReverseMap[selectedCategory] ?: "dairy_goods"
                },
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
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        productService.updateProduct(
                            foodItem = foodItem,
                            productName = productName,
                            quantity = quantity.toInt(),
                            unit = unit.value,
                            storageLocation = storageLocation.value,
                            category = category.value,
                            expiryDate = expiryDate,
                            isConsumedChecked = isConsumedChecked,
                            isThrownAwayChecked = isThrownAwayChecked,
                            coroutineScope = coroutineScope,
                            onSuccess = {
                                coroutineScope.launch {
                                    sheetState.hide()
                                }
                            },
                            onFailure = { e ->
                                Log.e(
                                    "ProductService",
                                    "Error updating product",
                                    e,
                                )
                            },
                            addedText = addedText,
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = WhiteColor),
            ) {
                Text(stringResource(R.string.save_changes), color = ComponentBackgroundColor)
            }
        }
    }
}

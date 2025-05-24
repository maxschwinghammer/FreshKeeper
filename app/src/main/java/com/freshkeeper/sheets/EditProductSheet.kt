package com.freshkeeper.sheets

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
import androidx.compose.foundation.layout.width
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
import com.freshkeeper.model.Category
import com.freshkeeper.model.FoodItem
import com.freshkeeper.model.FoodStatus
import com.freshkeeper.model.ImageType
import com.freshkeeper.model.StorageLocation
import com.freshkeeper.screens.home.DropdownMenu
import com.freshkeeper.screens.home.ExpiryDatePicker
import com.freshkeeper.screens.home.UnitSelector
import com.freshkeeper.service.PictureConverter
import com.freshkeeper.service.categoryMap
import com.freshkeeper.service.categoryReverseMap
import com.freshkeeper.service.categoryTips
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
    onUpdateProduct: (
        foodItem: FoodItem,
        productName: String,
        quantity: Int,
        unit: String,
        storageLocation: StorageLocation,
        category: Category,
        expiryTimestamp: Long,
        isConsumedChecked: Boolean,
        isThrownAwayChecked: Boolean,
    ) -> Unit,
) {
    val pictureConverter = PictureConverter()
    var productName by remember { mutableStateOf(foodItem.name) }
    var quantity by remember { mutableStateOf(foodItem.quantity.toString()) }
    val unit = remember { mutableStateOf(foodItem.unit) }
    val storageLocation = remember { mutableStateOf(foodItem.storageLocation) }
    val category = remember { mutableStateOf(foodItem.category) }
    var isConsumedChecked by remember { mutableStateOf(foodItem.status == FoodStatus.CONSUMED) }
    var isThrownAwayChecked by remember { mutableStateOf(foodItem.status == FoodStatus.THROWN_AWAY) }
    var expiryTimestamp by remember { mutableLongStateOf(foodItem.expiryTimestamp) }
    val picture = remember { mutableStateOf(foodItem.picture) }

    val selectedStorageLocation = storageLocationMap[storageLocation.value] ?: R.string.fridge
    val selectedCategory = categoryMap[category.value] ?: R.string.dairy_goods

    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = {
            if (sheetState.isVisible) {
                coroutineScope.launch { sheetState.hide() }
            }
        },
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
                        days == 0 -> stringResource(R.string.expired_today)
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
                        expiryTimestamp = expiryTimestamp,
                        onDateChange = { newDate ->
                            if (newDate != null) {
                                expiryTimestamp = newDate
                            }
                        },
                    )
                }

                val currentPicture = picture.value
                when (currentPicture?.type) {
                    ImageType.BASE64 -> {
                        val decodedImage =
                            currentPicture.image?.let { image ->
                                pictureConverter.convertBase64ToBitmap(image)
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
                    }
                    ImageType.URL -> {
                        if (!currentPicture.image.isNullOrEmpty()) {
                            Box(
                                modifier =
                                    Modifier
                                        .padding(top = 8.dp, start = 16.dp)
                                        .defaultMinSize(minWidth = 150.dp, minHeight = 129.dp)
                                        .weight(1f)
                                        .heightIn(max = 129.dp),
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(currentPicture.image),
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

                    null -> {}
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
                        storageLocation.value =
                            (
                                storageLocationReverseMap[selectedStorageLocation]
                                    ?: StorageLocation.FRIDGE
                            ) as StorageLocation
                    },
                    "storageLocations",
                    stringResource(R.string.storage_location),
                )

                Spacer(modifier = Modifier.height(8.dp))

                DropdownMenu(
                    selectedCategory,
                    onSelect = { selectedCategory ->
                        category.value = (categoryReverseMap[selectedCategory] ?: Category.DAIRY_GOODS) as Category
                    },
                    "categories",
                    stringResource(R.string.category),
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isConsumedChecked,
                        onCheckedChange = {
                            isConsumedChecked = it
                            if (it) isThrownAwayChecked = false
                        },
                        enabled = !isThrownAwayChecked,
                        modifier = Modifier.size(20.dp),
                        colors =
                            CheckboxDefaults.colors(
                                checkmarkColor = ComponentBackgroundColor,
                                checkedColor = AccentGreenColor,
                            ),
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        stringResource(R.string.mark_as_consumed),
                        color = TextColor,
                        fontSize = 16.sp,
                        lineHeight = 16.sp,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isThrownAwayChecked,
                        onCheckedChange = {
                            isThrownAwayChecked = it
                            if (it) isConsumedChecked = false
                        },
                        enabled = !isConsumedChecked,
                        modifier = Modifier.size(20.dp),
                        colors =
                            CheckboxDefaults.colors(
                                checkmarkColor = ComponentBackgroundColor,
                                checkedColor = RedColor,
                            ),
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        stringResource(R.string.mark_as_thrown_away),
                        color = TextColor,
                        fontSize = 16.sp,
                        lineHeight = 16.sp,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        coroutineScope.launch {
                            onUpdateProduct(
                                foodItem,
                                productName,
                                quantity.toInt(),
                                unit.value,
                                storageLocation.value,
                                category.value,
                                expiryTimestamp,
                                isConsumedChecked,
                                isThrownAwayChecked,
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = WhiteColor),
                ) {
                    Text(
                        stringResource(R.string.save_changes),
                        color = ComponentBackgroundColor,
                        maxLines = 1,
                        softWrap = false,
                    )
                }
            }
        }
    }
}

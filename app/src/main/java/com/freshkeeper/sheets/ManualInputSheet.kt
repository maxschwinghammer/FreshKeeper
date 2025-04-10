package com.freshkeeper.sheets

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.freshkeeper.R
import com.freshkeeper.screens.home.DropdownMenu
import com.freshkeeper.screens.home.ExpiryDatePicker
import com.freshkeeper.screens.home.UnitSelector
import com.freshkeeper.screens.profileSettings.compressImage
import com.freshkeeper.screens.profileSettings.convertBitmapToBase64
import com.freshkeeper.service.account.AccountServiceImpl
import com.freshkeeper.service.categoryMap
import com.freshkeeper.service.categoryReverseMap
import com.freshkeeper.service.product.ProductServiceImpl
import com.freshkeeper.service.productDetails.ProductDetailsServiceImpl
import com.freshkeeper.service.storageLocationMap
import com.freshkeeper.service.storageLocationReverseMap
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.TextColor
import com.freshkeeper.ui.theme.WhiteColor
import kotlinx.coroutines.launch

@Suppress("ktlint:standard:function-naming")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualInputSheet(
    sheetState: SheetState,
    barcode: String,
    expiryTimestamp: Long,
    recognizedFoodName: String,
) {
    val context = LocalContext.current
    val accountService = remember { AccountServiceImpl() }
    val productDetailsService = remember { ProductDetailsServiceImpl(context) }
    val productService = remember { ProductServiceImpl(accountService) }

    var productName by remember { mutableStateOf("") }
    var expiryDate by remember { mutableLongStateOf(expiryTimestamp) }
    var quantity by remember { mutableStateOf("") }
    val unit = remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    val defaultImageRes = R.drawable.default_product_image
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    var showAddImageButton by remember { mutableStateOf(true) }
    var showImageComposable by remember { mutableStateOf(false) }

    LaunchedEffect(barcode, recognizedFoodName) {
        if (barcode.isNotEmpty()) {
            val productData = productDetailsService.fetchProductDataFromBarcode(context, barcode)
            productName = productData?.name ?: ""
            quantity = productData?.quantity ?: ""
            unit.value = productData?.unit ?: ""
            imageUrl = productData?.imageUrl ?: ""
            showImageComposable = true
            showAddImageButton = false
        } else if (recognizedFoodName.isNotBlank()) {
            productName = recognizedFoodName
        }
    }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                imageUri = uri
                imageUrl = uri.toString()
            }
        }

    val cameraLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.TakePicturePreview(),
        ) { bitmap ->
            if (bitmap != null) {
                val contentValues =
                    ContentValues().apply {
                        put(
                            MediaStore.MediaColumns.DISPLAY_NAME,
                            "" + "Product_Image_${System.currentTimeMillis()}.png",
                        )
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                    }

                val uri =
                    context.contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues,
                    )

                uri?.let {
                    context.contentResolver.openOutputStream(it)?.use { outputStream ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    }
                    imageUri = it
                    imageUrl = it.toString()
                }
            }
        }

    val storageLocation = remember { mutableStateOf("fridge") }
    val category = remember { mutableStateOf("dairy_goods") }
    val coroutineScope = rememberCoroutineScope()

    val selectedStorageLocation = storageLocationMap[storageLocation.value] ?: R.string.fridge
    val selectedCategory = categoryMap[category.value] ?: R.string.dairy_goods

    val takePhoto = stringResource(R.string.take_photo)
    val selectFromGallery = stringResource(R.string.select_from_gallery)
    val productImage = stringResource(R.string.product_image)
    val addImage = stringResource(R.string.add_image)
    val changeImage = stringResource(R.string.change_image)
    val deleteImage = stringResource(R.string.delete_image)
    val cancel = stringResource(R.string.cancel)

    fun showImagePicker(
        context: Context,
        galleryLauncher: ManagedActivityResultLauncher<String, Uri?>,
        cameraLauncher: ManagedActivityResultLauncher<Void?, Bitmap?>,
    ) {
        val options = arrayOf(takePhoto, selectFromGallery, cancel)
        AlertDialog
            .Builder(context)
            .setTitle(addImage)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> cameraLauncher.launch(null)
                    1 -> galleryLauncher.launch("image/*")
                }
            }.show()
    }

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
            if (showAddImageButton) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.new_entry),
                        fontSize = 18.sp,
                        color = TextColor,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f).padding(start = 30.dp),
                        textAlign = TextAlign.Center,
                    )
                    Image(
                        painter = painterResource(R.drawable.add_image),
                        contentDescription = "Add image",
                        modifier =
                            Modifier
                                .padding(end = 10.dp)
                                .size(20.dp)
                                .clickable {
                                    showImageComposable = true
                                    showAddImageButton = false
                                },
                    )
                }
            } else {
                Text(
                    text = stringResource(R.string.new_entry),
                    fontSize = 18.sp,
                    color = TextColor,
                    style = MaterialTheme.typography.titleMedium,
                )
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

                if (showImageComposable) {
                    Box(
                        modifier =
                            Modifier
                                .padding(top = 8.dp, start = 16.dp)
                                .defaultMinSize(minWidth = 150.dp, minHeight = 129.dp)
                                .weight(1f)
                                .heightIn(max = 129.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp))
                                .clickable {
                                    val options =
                                        if (imageUrl.isEmpty()) {
                                            arrayOf(addImage, deleteImage, cancel)
                                        } else {
                                            arrayOf(changeImage, deleteImage, cancel)
                                        }

                                    AlertDialog
                                        .Builder(context)
                                        .setTitle(productImage)
                                        .setItems(options) { _, which ->
                                            when (which) {
                                                0 ->
                                                    showImagePicker(
                                                        context,
                                                        launcher,
                                                        cameraLauncher,
                                                    )
                                                1 -> {
                                                    if (imageUrl.isEmpty()) {
                                                        showImageComposable = false
                                                        showAddImageButton = true
                                                    } else {
                                                        imageUri = null
                                                        imageUrl = ""
                                                    }
                                                }
                                            }
                                        }.show()
                                },
                    ) {
                        Image(
                            painter =
                                if (imageUrl.isNotEmpty()) {
                                    rememberAsyncImagePainter(imageUrl)
                                } else {
                                    painterResource(defaultImageRes)
                                },
                            contentDescription = "Product Image",
                            contentScale = ContentScale.Fit,
                            modifier =
                                Modifier.fillMaxSize().then(
                                    if (imageUrl.isEmpty()) Modifier.padding(25.dp) else Modifier,
                                ),
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

            Spacer(modifier = Modifier.height(16.dp))

            val addedText = stringResource(R.string.added_product)
            val enterProductName = stringResource(R.string.enter_product_name)
            val selectExpiryDate = stringResource(R.string.select_expiry_date)
            val enterQuantity = stringResource(R.string.enter_quantity)
            val selectUnit = stringResource(R.string.select_unit)

            Button(
                onClick = {
                    if (productName.isBlank()) {
                        Toast.makeText(context, enterProductName, Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (expiryDate == 0L) {
                        Toast.makeText(context, selectExpiryDate, Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (quantity.isBlank() || quantity.toIntOrNull() == null || quantity.toInt() <= 0) {
                        Toast.makeText(context, enterQuantity, Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (unit.value.isBlank()) {
                        Toast.makeText(context, selectUnit, Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    coroutineScope.launch {
                        val householdId = accountService.getHouseholdId()
                        val image =
                            imageUri?.let { uri ->
                                compressImage(uri, context)?.let { bitmap ->
                                    convertBitmapToBase64(bitmap)
                                }
                            }
                        productService.addProduct(
                            productName,
                            barcode,
                            expiryDate,
                            quantity.toInt(),
                            unit.value,
                            storageLocation.value,
                            category.value,
                            image,
                            imageUrl,
                            householdId,
                            coroutineScope,
                            { coroutineScope.launch { sheetState.hide() } },
                            { e -> Log.e("Firestore", "Error when adding the product", e) },
                            addedText,
                        )
                    }
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
}

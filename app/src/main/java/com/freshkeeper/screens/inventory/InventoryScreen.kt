package com.freshkeeper.screens.inventory

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.freshkeeper.R
import com.freshkeeper.model.Category
import com.freshkeeper.model.FoodItem
import com.freshkeeper.model.StorageLocation
import com.freshkeeper.navigation.BottomNavigationBar
import com.freshkeeper.screens.LowerTransition
import com.freshkeeper.screens.UpperTransition
import com.freshkeeper.screens.inventory.viewmodel.InventoryViewModel
import com.freshkeeper.screens.notifications.viewmodel.NotificationsViewModel
import com.freshkeeper.service.categoryMap
import com.freshkeeper.service.storageLocationMap
import com.freshkeeper.sheets.AddProductSheet
import com.freshkeeper.sheets.BarcodeScannerSheet
import com.freshkeeper.sheets.EditProductSheet
import com.freshkeeper.sheets.FilterSheet
import com.freshkeeper.sheets.FoodRecognitionSheet
import com.freshkeeper.sheets.ManualInputSheet
import com.freshkeeper.sheets.productDetails.ProductDetailsSheet
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.BottomNavBackgroundColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.FreshKeeperTheme
import com.freshkeeper.ui.theme.GreyColor
import com.freshkeeper.ui.theme.TextColor
import com.freshkeeper.ui.theme.WhiteColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("ktlint:standard:function-naming")
@Composable
fun InventoryScreen(navController: NavHostController) {
    val viewModel: InventoryViewModel = hiltViewModel()
    val notificationsViewModel: NotificationsViewModel = hiltViewModel()
//    val homeViewModel: HomeViewModel = hiltViewModel()

    var scannedBarcode by remember { mutableStateOf<String?>(null) }
    var recognizedFoodName by remember { mutableStateOf<String?>(null) }
    var scannedExpiryDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
//    val isMember by homeViewModel.isMember.observeAsState()

    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val manualInputSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val editProductSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val barcodeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val foodRecognitionSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val filterSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val productInfoSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedCategories by remember { mutableStateOf(emptyList<Category>()) }
    var selectedStorageLocations by remember { mutableStateOf(emptyList<StorageLocation>()) }

    val items by viewModel.foodItems.observeAsState(emptyList())

    var foodItems by remember { mutableStateOf(emptyList<FoodItem>()) }
    var foodItem by remember { mutableStateOf<FoodItem?>(null) }

    val listState = rememberLazyListState()
    val showUpperTransition by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0
        }
    }
    val showLowerTransition by remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.size < items.size
        }
    }

    var searchQuery by remember { mutableStateOf("") }
    val labelMap = categoryMap + storageLocationMap

    fun getLabel(key: Any): Int? = labelMap[key]

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.getAllFoodItems(context)
    }

    FreshKeeperTheme {
        Scaffold(
            bottomBar = {
                Box(
                    modifier =
                        Modifier
                            .background(BottomNavBackgroundColor)
                            .padding(horizontal = 10.dp),
                ) {
                    BottomNavigationBar(selectedIndex = 1, navController, notificationsViewModel)
                }
            },
        ) { it ->
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(it),
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier =
                            Modifier
                                .padding(
                                    top = 16.dp,
                                    bottom = 8.dp,
                                    start = 16.dp,
                                    end = 16.dp,
                                ).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = stringResource(R.string.current_inventories),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextColor,
                            modifier = Modifier.weight(1f),
                        )

//                        if (isMember == true) {
                        Icon(
                            painter = painterResource(R.drawable.ai_chat),
                            tint = AccentTurquoiseColor,
                            contentDescription = "AI Chat",
                            modifier =
                                Modifier
                                    .size(25.dp)
                                    .clickable {
                                        navController.navigate("chat")
                                    },
                        )
//                        }
                    }
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            label = { Text(stringResource(R.string.search), color = TextColor) },
                            trailingIcon = {
                                Icon(Icons.Filled.Search, contentDescription = "Search")
                            },
                            modifier = Modifier.weight(1f),
                            colors =
                                OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = ComponentBackgroundColor,
                                    unfocusedContainerColor = ComponentBackgroundColor,
                                    disabledContainerColor = ComponentBackgroundColor,
                                    unfocusedBorderColor = ComponentStrokeColor,
                                    focusedBorderColor = ComponentStrokeColor,
                                    unfocusedLabelColor = TextColor,
                                    focusedLabelColor = AccentTurquoiseColor,
                                    cursorColor = AccentTurquoiseColor,
                                ),
                            keyboardOptions =
                                KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                            keyboardActions =
                                KeyboardActions(
                                    onDone = {
                                        keyboardController?.hide()
                                    },
                                ),
                            shape = RoundedCornerShape(10.dp),
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            modifier =
                                Modifier
                                    .padding(top = 8.dp)
                                    .height(57.dp)
                                    .width(55.dp)
                                    .background(ComponentBackgroundColor)
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp))
                                    .clickable {
                                        coroutineScope.launch {
                                            filterSheetState.show()
                                        }
                                    },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.filter),
                                contentDescription = "Filter",
                                tint = WhiteColor,
                                modifier =
                                    Modifier
                                        .size(20.dp),
                            )
                        }
                    }
                    if (selectedCategories.isNotEmpty() || selectedStorageLocations.isNotEmpty()) {
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            (selectedCategories + selectedStorageLocations).forEach { filter ->
                                Box(
                                    modifier =
                                        Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(GreyColor)
                                            .border(
                                                1.dp,
                                                ComponentStrokeColor,
                                                RoundedCornerShape(20.dp),
                                            ).clickable {
                                                if (selectedCategories.contains(filter)) {
                                                    selectedCategories = selectedCategories -
                                                        (filter as Category)
                                                } else {
                                                    selectedStorageLocations =
                                                        selectedStorageLocations -
                                                        (filter as StorageLocation)
                                                }
                                            }.padding(horizontal = 12.dp, vertical = 6.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        val labelId = getLabel(filter)

                                        Text(
                                            text =
                                                if (labelId != null) {
                                                    stringResource(id = labelId)
                                                } else {
                                                    ""
                                                },
                                            fontSize = 12.sp,
                                            color = TextColor,
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = null,
                                            tint = TextColor,
                                            modifier = Modifier.size(14.dp),
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Box(
                        modifier =
                            Modifier
                                .weight(1f)
                                .padding(bottom = 5.dp),
                    ) {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.padding(top = 10.dp, start = 15.dp, end = 15.dp),
                        ) {
                            item {
                                CurrentInventoriesSection(
                                    editProductSheetState = editProductSheetState,
                                    onItemClick = { item -> foodItem = item },
                                    selectedStorageLocations = selectedStorageLocations,
                                    selectedCategories = selectedCategories,
                                    searchQuery = searchQuery,
                                    onItemsUpdated = { items -> foodItems = items },
                                )
                            }
                            item {
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                        if (showUpperTransition) {
                            UpperTransition()
                        }
                        if (showLowerTransition) {
                            LowerTransition(
                                modifier = Modifier.align(Alignment.BottomCenter),
                            )
                        }
                    }

                    Button(
                        onClick = { coroutineScope.launch { sheetState.show() } },
                        modifier =
                            Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(bottom = 10.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = ComponentBackgroundColor,
                            ),
                        border = BorderStroke(1.dp, ComponentStrokeColor),
                        shape = RoundedCornerShape(25.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            FloatingActionButton(
                                onClick = { coroutineScope.launch { sheetState.show() } },
                                modifier = Modifier.size(35.dp),
                                shape = RoundedCornerShape(25.dp),
                                containerColor = AccentTurquoiseColor,
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    tint = BottomNavBackgroundColor,
                                    contentDescription = "Add Food",
                                )
                            }
                            Text(
                                text = stringResource(id = R.string.add_food),
                                style = MaterialTheme.typography.titleMedium,
                                color = AccentTurquoiseColor,
                                modifier = Modifier.padding(start = 10.dp),
                            )
                        }
                    }
                }
            }

            if (sheetState.isVisible) {
                AddProductSheet(
                    sheetState,
                    barcodeSheetState,
                    foodRecognitionSheetState,
                    manualInputSheetState,
                )
            }

            if (barcodeSheetState.isVisible) {
                BarcodeScannerSheet(
                    sheetState = barcodeSheetState,
                    onBarcodeScanned = { barcode, expiryDate ->
                        scannedBarcode = barcode
                        scannedExpiryDate = expiryDate
                        coroutineScope.launch { manualInputSheetState.show() }
                    },
                )
            }

            if (foodRecognitionSheetState.isVisible) {
                FoodRecognitionSheet(
                    sheetState = foodRecognitionSheetState,
                    onFoodRecognized = { recognizedFood ->
                        println("Recognised food: $recognizedFood")
                        recognizedFoodName = recognizedFood
                        coroutineScope.launch { manualInputSheetState.show() }
                    },
                )
            }

            if (manualInputSheetState.isVisible) {
                ManualInputSheet(
                    sheetState = manualInputSheetState,
                    barcode = scannedBarcode,
                    scannedExpiryDate = scannedExpiryDate,
                    recognizedFoodName = recognizedFoodName,
                    onFetchProductDataFromBarcode = { barcode, onSuccess, onFailure ->
                        coroutineScope.launch {
                            viewModel.fetchProductDataFromBarcode(barcode, onSuccess, onFailure)
                        }
                    },
                    onAddProduct = {
                        name,
                        barcode,
                        expiryTimestamp,
                        qty,
                        unit,
                        storage,
                        category,
                        image,
                        imageUrl,
                        ->
                        viewModel.addProduct(
                            name,
                            barcode,
                            expiryTimestamp,
                            qty,
                            unit,
                            storage,
                            category,
                            image,
                            imageUrl,
                            coroutineScope,
                            context,
                            onSuccess = { coroutineScope.launch { manualInputSheetState.hide() } },
                        )
                    },
                )
            }

            if (editProductSheetState.isVisible) {
                foodItem?.let { item ->
                    EditProductSheet(
                        editProductSheetState,
                        productInfoSheetState,
                        item,
                        onUpdateProduct = {
                            foodItem,
                            productName,
                            quantity,
                            unit,
                            storageLocation,
                            category,
                            expiryTimestamp,
                            isConsumedChecked,
                            isThrownAwayChecked,
                            ->
                            viewModel.updateProduct(
                                foodItem,
                                productName,
                                quantity,
                                unit,
                                storageLocation,
                                category,
                                expiryTimestamp,
                                isConsumedChecked,
                                isThrownAwayChecked,
                                coroutineScope,
                                onSuccess = {
                                    coroutineScope.launch { editProductSheetState.hide() }
                                },
                            )
                        },
                    )
                }
            }

            if (filterSheetState.isVisible) {
                FilterSheet(
                    sheetState = filterSheetState,
                    foodItems = items,
                    selectedCategories = selectedCategories,
                    selectedStorageLocations = selectedStorageLocations,
                    onUpdateCategories = { updatedCategories ->
                        selectedCategories = updatedCategories.toList()
                    },
                    onUpdateStorageLocations = { updatedLocations ->
                        selectedStorageLocations = updatedLocations.toList()
                    },
                    onApplyFilter = { categories, locations ->
                        foodItems =
                            foodItems.filter {
                                (categories.isEmpty() || categories.contains(it.category)) &&
                                    (locations.isEmpty() || locations.contains(it.storageLocation))
                            }
                        selectedCategories = categories
                        selectedStorageLocations = locations
                    },
                )
            }

            if (productInfoSheetState.isVisible) {
                foodItem?.let { item ->
                    ProductDetailsSheet(productInfoSheetState, editProductSheetState, item)
                }
            }
        }
    }
}

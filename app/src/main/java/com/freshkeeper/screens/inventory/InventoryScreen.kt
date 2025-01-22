package com.freshkeeper.screens.inventory

import android.util.Log
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.freshkeeper.R
import com.freshkeeper.model.FoodItem
import com.freshkeeper.navigation.BottomNavigationBar
import com.freshkeeper.screens.LowerTransition
import com.freshkeeper.screens.UpperTransition
import com.freshkeeper.screens.inventory.viewmodel.InventoryViewModel
import com.freshkeeper.screens.notifications.viewmodel.NotificationsViewModel
import com.freshkeeper.sheets.AddEntrySheet
import com.freshkeeper.sheets.BarcodeScannerSheet
import com.freshkeeper.sheets.EditProductSheet
import com.freshkeeper.sheets.FilterSheet
import com.freshkeeper.sheets.ManualInputSheet
import com.freshkeeper.ui.theme.AccentGreenColor
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.BottomNavBackgroundColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.FreshKeeperTheme
import com.freshkeeper.ui.theme.GreyColor
import com.freshkeeper.ui.theme.TextColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("ktlint:standard:function-naming")
@Composable
fun InventoryScreen(navController: NavHostController) {
    val notificationsViewModel: NotificationsViewModel = hiltViewModel()
    val inventoryViewModel: InventoryViewModel = hiltViewModel()

    var scannedBarcode by remember { mutableStateOf("") }
    var expiryDate by remember { mutableLongStateOf(0L) }

    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val manualInputSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val editProductSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val barcodeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val filterSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedCategories by remember { mutableStateOf(emptyList<String>()) }
    var selectedStorageLocations by remember { mutableStateOf(emptyList<String>()) }

    val items by inventoryViewModel.foodItems.observeAsState(emptyList())

    Log.d("InventoryScreen", "filter food items: $items")

    var foodItems by remember { mutableStateOf(emptyList<FoodItem>()) }
    var foodItem by remember { mutableStateOf<FoodItem?>(null) }

    val listState = rememberLazyListState()
    val showTransition by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0
        }
    }

    var searchQuery by remember { mutableStateOf("") }

    val storageLocationMap =
        mapOf(
            "fridge" to R.string.fridge,
            "cupboard" to R.string.cupboard,
            "freezer" to R.string.freezer,
            "counter_top" to R.string.counter_top,
            "cellar" to R.string.cellar,
            "bread_box" to R.string.bread_box,
            "spice_rack" to R.string.spice_rack,
            "pantry" to R.string.pantry,
            "fruit_basket" to R.string.fruit_basket,
            "other" to R.string.other,
        )

    val categoryMap =
        mapOf(
            "dairy_goods" to R.string.dairy_goods,
            "vegetables" to R.string.vegetables,
            "fruits" to R.string.fruits,
            "meat" to R.string.meat,
            "fish" to R.string.fish,
            "frozen_goods" to R.string.frozen_goods,
            "spices" to R.string.spices,
            "bread" to R.string.bread,
            "confectionery" to R.string.confectionery,
            "drinks" to R.string.drinks,
            "noodles" to R.string.noodles,
            "canned_goods" to R.string.canned_goods,
            "candy" to R.string.candy,
            "other" to R.string.other,
        )

    val labelMap = categoryMap + storageLocationMap

    fun getLabel(key: String): Int? = labelMap[key]

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
                    Text(
                        text = stringResource(R.string.current_inventories),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextColor,
                        modifier = Modifier.padding(top = 16.dp, end = 16.dp, start = 16.dp),
                    )
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            label = { Text(stringResource(R.string.search), color = TextColor) },
                            trailingIcon = {
                                IconButton(onClick = { /* Handle search action */ }) {
                                    Icon(Icons.Filled.Search, contentDescription = "Search")
                                }
                            },
                            modifier =
                                Modifier
                                    .weight(1f),
                            colors =
                                OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = ComponentStrokeColor,
                                    focusedBorderColor = AccentTurquoiseColor,
                                    unfocusedLabelColor = TextColor,
                                    focusedLabelColor = AccentTurquoiseColor,
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
                                    .background(Color.Transparent)
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp))
                                    .clickable { coroutineScope.launch { filterSheetState.show() } },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.filter),
                                contentDescription = "Filter",
                                tint = Color.White,
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
                                            .border(1.dp, ComponentStrokeColor, RoundedCornerShape(20.dp))
                                            .clickable {
                                                if (selectedCategories.contains(filter)) {
                                                    selectedCategories = selectedCategories - filter
                                                } else {
                                                    selectedStorageLocations = selectedStorageLocations - filter
                                                }
                                            }.padding(horizontal = 12.dp, vertical = 6.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        val labelId = getLabel(filter)

                                        Text(
                                            text = if (labelId != null) stringResource(id = labelId) else "",
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
                                    onItemClick = { item ->
                                        foodItem = item
                                    },
                                    selectedStorageLocations = selectedStorageLocations,
                                    selectedCategories = selectedCategories,
                                    onItemsUpdated = { items ->
                                        foodItems = items
                                    },
                                )
                            }
                            item {
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                        if (showTransition) {
                            UpperTransition()
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
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
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
                                containerColor = AccentGreenColor,
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
                                color = AccentGreenColor,
                                modifier = Modifier.padding(start = 10.dp),
                            )
                        }
                    }
                }
            }

            if (sheetState.isVisible) {
                AddEntrySheet(sheetState, barcodeSheetState, manualInputSheetState)
            }

            if (barcodeSheetState.isVisible) {
                ModalBottomSheet(
                    onDismissRequest = { coroutineScope.launch { barcodeSheetState.hide() } },
                    sheetState = barcodeSheetState,
                    containerColor = ComponentBackgroundColor,
                ) {
                    BarcodeScannerSheet(
                        sheetState = barcodeSheetState,
                        onBarcodeScanned = { barcode, date ->
                            scannedBarcode = barcode
                            expiryDate = date
                            coroutineScope.launch { manualInputSheetState.show() }
                        },
                    )
                }
            }

            if (manualInputSheetState.isVisible) {
                ModalBottomSheet(
                    onDismissRequest = { coroutineScope.launch { manualInputSheetState.hide() } },
                    sheetState = manualInputSheetState,
                    containerColor = ComponentBackgroundColor,
                ) {
                    ManualInputSheet(
                        sheetState = manualInputSheetState,
                        barcode = scannedBarcode,
                        expiryTimestamp = expiryDate,
                    )
                }
            }

            if (editProductSheetState.isVisible) {
                ModalBottomSheet(
                    onDismissRequest = { coroutineScope.launch { editProductSheetState.hide() } },
                    sheetState = editProductSheetState,
                    containerColor = ComponentBackgroundColor,
                ) {
                    foodItem?.let { item ->
                        EditProductSheet(editProductSheetState, item)
                    }
                }
            }

            if (filterSheetState.isVisible) {
                ModalBottomSheet(
                    onDismissRequest = { coroutineScope.launch { filterSheetState.hide() } },
                    sheetState = filterSheetState,
                    containerColor = ComponentBackgroundColor,
                ) {
                    FilterSheet(
                        filterSheetState = filterSheetState,
                        foodItems = items,
                        categories = categoryMap,
                        storageLocations = storageLocationMap,
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
                                    (
                                        categories.isEmpty() ||
                                            categories.contains(it.category)
                                    ) &&
                                        (
                                            locations.isEmpty() ||
                                                locations.contains(it.storageLocation)
                                        )
                                }
                            selectedCategories = categories
                            selectedStorageLocations = locations
                        },
                    )
                }
            }
        }
    }
}

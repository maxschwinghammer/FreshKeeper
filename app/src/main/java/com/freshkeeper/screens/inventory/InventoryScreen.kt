package com.freshkeeper.screens.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.freshkeeper.R
import com.freshkeeper.navigation.BottomNavigationBar
import com.freshkeeper.screens.inventory.service.CurrentInventoriesSection
import com.freshkeeper.screens.notifications.NotificationsViewModel
import com.freshkeeper.sheets.AddEntrySheet
import com.freshkeeper.sheets.BarcodeScannerSheet
import com.freshkeeper.sheets.EditProductSheet
import com.freshkeeper.sheets.ManualInputSheet
import com.freshkeeper.ui.theme.AccentGreenColor
import com.freshkeeper.ui.theme.BottomNavBackgroundColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.FreshKeeperTheme
import com.freshkeeper.ui.theme.TextColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("ktlint:standard:function-naming")
@Composable
fun InventoryScreen(
    navController: NavHostController,
    notificationsViewModel: NotificationsViewModel,
) {
    var scannedBarcode by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val manualInputSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val editProductSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val barcodeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(it),
            ) {
                Text(
                    text = stringResource(R.string.current_inventories),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextColor,
                    modifier = Modifier.padding(16.dp),
                )
                LazyColumn(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(top = 55.dp, start = 15.dp, end = 15.dp, bottom = 70.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    item {
                        CurrentInventoriesSection(editProductSheetState = editProductSheetState)
                    }
                }
                Button(
                    onClick = { coroutineScope.launch { sheetState.show() } },
                    modifier =
                        Modifier
                            .align(Alignment.BottomCenter)
                            .padding(10.dp)
                            .clip(RoundedCornerShape(25.dp))
                            .border(1.dp, ComponentStrokeColor, RoundedCornerShape(25.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
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
                        barcodeValue = scannedBarcode,
                        expiryDateValue = expiryDate,
                    )
                }
            }

            if (editProductSheetState.isVisible) {
                ModalBottomSheet(
                    onDismissRequest = { coroutineScope.launch { editProductSheetState.hide() } },
                    sheetState = editProductSheetState,
                    containerColor = ComponentBackgroundColor,
                ) {
                    EditProductSheet(
                        sheetState = editProductSheetState,
                        expiryDateValue = expiryDate,
                    )
                }
            }
        }
    }
}

package com.freshkeeper.screens.home

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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.freshkeeper.R
import com.freshkeeper.navigation.BottomNavigationBar
import com.freshkeeper.screens.home.service.FoodList
import com.freshkeeper.screens.home.sheets.AddEntrySheet
import com.freshkeeper.screens.home.sheets.BarcodeScannerSheet
import com.freshkeeper.screens.home.sheets.ManualInputSheet
import com.freshkeeper.screens.home.viewmodel.HomeViewModel
import com.freshkeeper.ui.theme.AccentGreenColor
import com.freshkeeper.ui.theme.BottomNavBackgroundColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.FreshKeeperTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("ktlint:standard:function-naming")
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = viewModel(),
) {
    var scannedBarcode by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    val manualInputSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val barcodeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val expiringSoonItems by viewModel.expiringSoonItems.observeAsState(emptyList())
    val expiredItems by viewModel.expiredItems.observeAsState(emptyList())

    FreshKeeperTheme {
        Scaffold(
            bottomBar = {
                Box(
                    modifier =
                        Modifier
                            .background(BottomNavBackgroundColor)
                            .padding(horizontal = 10.dp),
                ) {
                    BottomNavigationBar(selectedIndex = 0, navController)
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
                    text = "Overview",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(16.dp),
                )
                LazyColumn(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(top = 50.dp, start = 10.dp, end = 10.dp, bottom = 60.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    item {
                        FoodList(
                            title = "Expiring soon",
                            image = painterResource(id = R.drawable.expiring_soon),
                            items = expiringSoonItems,
                        )
                    }
                    item {
                        FoodList(
                            title = "Expired",
                            image = painterResource(id = R.drawable.warning),
                            items = expiredItems,
                        )
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
                            Icon(Icons.Default.Add, tint = BottomNavBackgroundColor, contentDescription = "Add Food")
                        }
                        Text(
                            text = "Add food",
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
        }
    }
}

package com.freshkeeper.sheets

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freshkeeper.model.FoodItem
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.FreshKeeperTheme
import com.freshkeeper.ui.theme.GreyColor
import com.freshkeeper.ui.theme.TextColor
import kotlinx.coroutines.launch

@Suppress("ktlint:standard:function-naming")
@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class,
)
@Composable
fun FilterSheet(
    filterSheetState: SheetState,
    foodItems: List<FoodItem>,
    categories: Map<String, Int>,
    storageLocations: Map<String, Int>,
    selectedCategories: List<String>,
    selectedStorageLocations: List<String>,
    onUpdateCategories: (List<String>) -> Unit,
    onUpdateStorageLocations: (List<String>) -> Unit,
    onApplyFilter: (List<String>, List<String>) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    val displayedFoodItems =
        if (selectedCategories.isEmpty() && selectedStorageLocations.isEmpty()) {
            foodItems
        } else {
            foodItems.filter {
                (selectedCategories.isEmpty() || selectedCategories.contains(it.category)) &&
                    (selectedStorageLocations.isEmpty() || selectedStorageLocations.contains(it.storageLocation))
            }
        }

    Log.d("FilterSheet", "Displayed food items: $displayedFoodItems")

    FreshKeeperTheme {
        ModalBottomSheet(
            onDismissRequest = { coroutineScope.launch { filterSheetState.hide() } },
            sheetState = filterSheetState,
            containerColor = ComponentBackgroundColor,
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
            ) {
                Text(
                    text = "Filter (${displayedFoodItems.size})",
                    fontSize = 20.sp,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp),
                )

                Text(
                    text = "Categories",
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.bodyMedium,
                )

                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    categories.forEach { (key, value) ->
                        val isSelected = selectedCategories.contains(key)
                        Button(
                            onClick = {
                                val updatedCategories =
                                    if (isSelected) {
                                        selectedCategories - key
                                    } else {
                                        selectedCategories + key
                                    }
                                onUpdateCategories(updatedCategories)
                                onApplyFilter(
                                    updatedCategories.toList(),
                                    selectedStorageLocations.toList(),
                                )
                            },
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor = GreyColor,
                                    contentColor = TextColor,
                                ),
                            shape = RoundedCornerShape(20.dp),
                            border =
                                BorderStroke(
                                    1.dp,
                                    if (isSelected) {
                                        AccentTurquoiseColor
                                    } else {
                                        Color.Transparent
                                    },
                                ),
                            modifier = Modifier.padding(vertical = 2.dp, horizontal = 4.dp),
                        ) {
                            Text(text = stringResource(value), fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Storage Locations",
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.bodyMedium,
                )

                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    storageLocations.forEach { (key, value) ->
                        val isSelected = selectedStorageLocations.contains(key)
                        Button(
                            onClick = {
                                val updatedLocations =
                                    if (isSelected) {
                                        selectedStorageLocations - key
                                    } else {
                                        selectedStorageLocations + key
                                    }
                                onUpdateStorageLocations(updatedLocations)
                                onApplyFilter(
                                    selectedCategories.toList(),
                                    updatedLocations.toList(),
                                )
                            },
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor = GreyColor,
                                    contentColor = TextColor,
                                ),
                            shape = RoundedCornerShape(20.dp),
                            border =
                                BorderStroke(
                                    1.dp,
                                    if (isSelected) {
                                        AccentTurquoiseColor
                                    } else {
                                        Color.Transparent
                                    },
                                ),
                            modifier = Modifier.padding(vertical = 2.dp, horizontal = 4.dp),
                        ) {
                            Text(text = stringResource(value), fontSize = 12.sp)
                        }
                    }
                }

                Button(
                    onClick = { coroutineScope.launch { filterSheetState.hide() } },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentTurquoiseColor),
                    border = BorderStroke(1.dp, ComponentStrokeColor),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                ) {
                    Text(text = "Apply Filter", color = Color.White)
                }
            }
        }
    }
}

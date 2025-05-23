package com.freshkeeper.sheets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import com.freshkeeper.R
import com.freshkeeper.model.Category
import com.freshkeeper.model.FoodItem
import com.freshkeeper.model.StorageLocation
import com.freshkeeper.service.categoryMap
import com.freshkeeper.service.storageLocationMap
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
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
    sheetState: SheetState,
    foodItems: List<FoodItem>,
    selectedCategories: List<Category>,
    selectedStorageLocations: List<StorageLocation>,
    onUpdateCategories: (List<Category>) -> Unit,
    onUpdateStorageLocations: (List<StorageLocation>) -> Unit,
    onApplyFilter: (List<Category>, List<StorageLocation>) -> Unit,
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

    FreshKeeperTheme {
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
                        .padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
            ) {
                Text(
                    text = stringResource(R.string.filter) + " (${displayedFoodItems.size})",
                    fontSize = 18.sp,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp),
                )

                Text(
                    text = stringResource(R.string.categories),
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp),
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    categoryMap.forEach { (key, value) ->
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
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                            modifier = Modifier.padding(vertical = 0.dp, horizontal = 4.dp),
                        ) {
                            Text(text = stringResource(value), fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.storage_locations),
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.bodyMedium,
                )

                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    storageLocationMap.forEach { (key, value) ->
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
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                            modifier = Modifier.padding(vertical = 0.dp, horizontal = 4.dp),
                        ) {
                            Text(text = stringResource(value), fontSize = 12.sp)
                        }
                    }
                }

                Button(
                    onClick = { coroutineScope.launch { sheetState.hide() } },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentTurquoiseColor),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                ) {
                    Text(text = stringResource(R.string.apply_filter), color = ComponentBackgroundColor)
                }
            }
        }
    }
}

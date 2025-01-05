package com.freshkeeper.screens.inventory

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.freshkeeper.R

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("ktlint:standard:function-naming")
@Composable
fun CurrentInventoriesSection(
    viewModel: InventoryViewModel = viewModel(),
    editProductSheetState: SheetState,
) {
    val fridgeItems by viewModel.fridgeItems.observeAsState(emptyList())
    val cupboardItems by viewModel.cupboardItems.observeAsState(emptyList())
    val freezerItems by viewModel.freezerItems.observeAsState(emptyList())
    val countertopItems by viewModel.countertopItems.observeAsState(emptyList())
    val cellarItems by viewModel.cellarItems.observeAsState(emptyList())
    val bakeryItems by viewModel.bakeryItems.observeAsState(emptyList())
    val spiceItems by viewModel.spicesItems.observeAsState(emptyList())
    val pantryItems by viewModel.pantryItems.observeAsState(emptyList())
    val fruitBasketItems by viewModel.fruitBasketItems.observeAsState(emptyList())
    val otherItems by viewModel.otherItems.observeAsState(emptyList())

    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        if (fridgeItems.isNotEmpty()) {
            InventoryCategory(
                title = stringResource(R.string.fridge),
                image = painterResource(id = R.drawable.fridge),
                items = fridgeItems,
                editProductSheetState = editProductSheetState,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (cupboardItems.isNotEmpty()) {
            InventoryCategory(
                title = stringResource(R.string.cupboard),
                image = painterResource(id = R.drawable.cupboard),
                items = cupboardItems,
                editProductSheetState = editProductSheetState,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (freezerItems.isNotEmpty()) {
            InventoryCategory(
                title = stringResource(R.string.freezer),
                image = painterResource(id = R.drawable.freezer),
                items = freezerItems,
                editProductSheetState = editProductSheetState,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (countertopItems.isNotEmpty()) {
            InventoryCategory(
                title = stringResource(R.string.counter_top),
                image = painterResource(id = R.drawable.counter_top),
                items = countertopItems,
                editProductSheetState = editProductSheetState,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (cellarItems.isNotEmpty()) {
            InventoryCategory(
                title = stringResource(R.string.cellar),
                image = painterResource(id = R.drawable.cellar),
                items = cellarItems,
                editProductSheetState = editProductSheetState,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (bakeryItems.isNotEmpty()) {
            InventoryCategory(
                title = stringResource(R.string.bread_box),
                image = painterResource(id = R.drawable.bread_box),
                items = bakeryItems,
                editProductSheetState = editProductSheetState,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (spiceItems.isNotEmpty()) {
            InventoryCategory(
                title = stringResource(R.string.spice_rack),
                image = painterResource(id = R.drawable.spice_rack),
                items = spiceItems,
                editProductSheetState = editProductSheetState,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (pantryItems.isNotEmpty()) {
            InventoryCategory(
                title = stringResource(R.string.pantry),
                image = painterResource(id = R.drawable.pantry),
                items = pantryItems,
                editProductSheetState = editProductSheetState,
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        if (fruitBasketItems.isNotEmpty()) {
            InventoryCategory(
                title = stringResource(R.string.fruit_basket),
                image = painterResource(id = R.drawable.fruit_basket),
                items = fruitBasketItems,
                editProductSheetState = editProductSheetState,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (otherItems.isNotEmpty()) {
            InventoryCategory(
                title = stringResource(R.string.other),
                image = painterResource(id = R.drawable.other),
                items = otherItems,
                editProductSheetState = editProductSheetState,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

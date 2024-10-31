package com.freshkeeper.screens.inventory.service

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.freshkeeper.R
import com.freshkeeper.screens.home.viewmodel.InventoryViewModel

@Suppress("ktlint:standard:function-naming")
@Composable
fun CurrentInventoriesSection(viewModel: InventoryViewModel = viewModel()) {
    val fridgeItems by viewModel.fridgeItems.observeAsState(emptyList())
    val cupboardItems by viewModel.cupboardItems.observeAsState(emptyList())
    val freezerItems by viewModel.freezerItems.observeAsState(emptyList())
    val countertopItems by viewModel.countertopItems.observeAsState(emptyList())
    val cellarItems by viewModel.cellarItems.observeAsState(emptyList())
    val bakeryItems by viewModel.bakeryItems.observeAsState(emptyList())
    val spiceItems by viewModel.spicesItems.observeAsState(emptyList())
    val pantryItems by viewModel.pantryItems.observeAsState(emptyList())

    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        InventoryCategory(
            title = "Fridge",
            image = painterResource(id = R.drawable.fridge),
            items = fridgeItems,
        )

        Spacer(modifier = Modifier.height(16.dp))

        InventoryCategory(
            title = "Cupboard",
            image = painterResource(id = R.drawable.cupboard),
            items = cupboardItems,
        )

        Spacer(modifier = Modifier.height(16.dp))

        InventoryCategory(
            title = "Freezer",
            image = painterResource(id = R.drawable.freezer),
            items = freezerItems,
        )

        Spacer(modifier = Modifier.height(16.dp))

        InventoryCategory(
            title = "Counter top",
            image = painterResource(id = R.drawable.counter_top),
            items = countertopItems,
        )

        Spacer(modifier = Modifier.height(16.dp))

        InventoryCategory(
            title = "Cellar",
            image = painterResource(id = R.drawable.cellar),
            items = cellarItems,
        )

        Spacer(modifier = Modifier.height(16.dp))

        InventoryCategory(
            title = "Bread box",
            image = painterResource(id = R.drawable.bread_box),
            items = bakeryItems,
        )

        Spacer(modifier = Modifier.height(16.dp))

        InventoryCategory(
            title = "Spice rack",
            image = painterResource(id = R.drawable.spice_rack),
            items = spiceItems,
        )

        Spacer(modifier = Modifier.height(16.dp))

        InventoryCategory(
            title = "Pantry",
            image = painterResource(id = R.drawable.pantry),
            items = pantryItems,
        )
    }
}

package com.freshkeeper.screens.landingpage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.freshkeeper.R
import com.freshkeeper.model.FoodItem
import com.freshkeeper.screens.inventory.StorageLocation

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("ktlint:standard:function-naming")
@Composable
fun Story2() {
    val editProductSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val fridgeItems =
        listOf(
            FoodItem(
                name = stringResource(R.string.food_tofu_natural),
                storageLocation = "Fridge",
                userId = "",
                householdId = "",
                expiryTimestamp = 0L,
                quantity = 1,
                unit = "",
                category = "",
                consumed = false,
                thrownAway = false,
                daysDifference = 10,
            ),
            FoodItem(
                name = stringResource(R.string.food_serrano_ham),
                storageLocation = "Fridge",
                userId = "",
                householdId = "",
                expiryTimestamp = 4047353600000,
                quantity = 1,
                unit = "",
                category = "",
                consumed = false,
                thrownAway = false,
                daysDifference = 10,
            ),
            FoodItem(
                name = stringResource(R.string.food_mozzarella),
                storageLocation = "Fridge",
                userId = "",
                householdId = "",
                expiryTimestamp = 0L,
                quantity = 1,
                unit = "",
                category = "",
                consumed = false,
                thrownAway = false,
                daysDifference = 10,
            ),
            FoodItem(
                name = stringResource(R.string.food_yogurt),
                storageLocation = "Fridge",
                userId = "",
                householdId = "",
                expiryTimestamp = 4047353600000,
                quantity = 1,
                unit = "",
                category = "",
                consumed = false,
                thrownAway = false,
                daysDifference = 10,
            ),
            FoodItem(
                name = stringResource(R.string.food_hummus),
                storageLocation = "Fridge",
                userId = "",
                householdId = "",
                expiryTimestamp = 4047353600000,
                quantity = 1,
                unit = "",
                category = "",
                consumed = false,
                thrownAway = false,
                daysDifference = 10,
            ),
        )

    val freezerItems =
        listOf(
            FoodItem(
                name = stringResource(R.string.food_edamame),
                storageLocation = "Freezer",
                userId = "",
                householdId = "",
                expiryTimestamp = 4047353600000,
                quantity = 1,
                unit = "",
                category = "",
                consumed = false,
                thrownAway = false,
                daysDifference = 10,
            ),
            FoodItem(
                name = stringResource(R.string.food_falafel),
                storageLocation = "Freezer",
                userId = "",
                householdId = "",
                expiryTimestamp = 4047353600000,
                quantity = 1,
                unit = "",
                category = "",
                consumed = false,
                thrownAway = false,
                daysDifference = 10,
            ),
            FoodItem(
                name = stringResource(R.string.food_spinach),
                storageLocation = "Freezer",
                userId = "",
                householdId = "",
                expiryTimestamp = 4047353600000,
                quantity = 1,
                unit = "",
                category = "",
                consumed = false,
                thrownAway = false,
                daysDifference = 10,
            ),
            FoodItem(
                name = stringResource(R.string.food_gyoza),
                storageLocation = "Freezer",
                userId = "",
                householdId = "",
                expiryTimestamp = 0L,
                quantity = 1,
                unit = "",
                category = "",
                consumed = false,
                thrownAway = false,
                daysDifference = 10,
            ),
            FoodItem(
                name = stringResource(R.string.food_ice),
                storageLocation = "Freezer",
                userId = "",
                householdId = "",
                expiryTimestamp = 0L,
                quantity = 1,
                unit = "",
                category = "",
                consumed = false,
                thrownAway = false,
                daysDifference = 10,
            ),
        )

    val cellarItems =
        listOf(
            FoodItem(
                name = stringResource(R.string.food_pickled_vegetables),
                storageLocation = "Cellar",
                userId = "",
                householdId = "",
                expiryTimestamp = 4047353600000,
                quantity = 1,
                unit = "",
                category = "",
                consumed = false,
                thrownAway = false,
                daysDifference = 10,
            ),
            FoodItem(
                name = stringResource(R.string.food_onions),
                storageLocation = "Cellar",
                userId = "",
                householdId = "",
                expiryTimestamp = 4047353600000,
                quantity = 2,
                unit = "",
                category = "",
                consumed = false,
                thrownAway = false,
                daysDifference = 10,
            ),
            FoodItem(
                name = stringResource(R.string.food_wine),
                storageLocation = "Cellar",
                userId = "",
                householdId = "",
                expiryTimestamp = 0L,
                quantity = 1,
                unit = "",
                category = "",
                consumed = false,
                thrownAway = false,
                daysDifference = 10,
            ),
        )

    val pantryItems =
        listOf(
            FoodItem(
                name = stringResource(R.string.food_coconut_milk),
                storageLocation = "Pantry",
                userId = "",
                householdId = "",
                expiryTimestamp = 4047353600000,
                quantity = 1,
                unit = "",
                category = "",
                consumed = false,
                thrownAway = false,
                daysDifference = 10,
            ),
            FoodItem(
                name = stringResource(R.string.food_spaghetti),
                storageLocation = "Pantry",
                userId = "",
                householdId = "",
                expiryTimestamp = 4047353600000,
                quantity = 1,
                unit = "",
                category = "",
                consumed = false,
                thrownAway = false,
                daysDifference = 10,
            ),
        )

    val storageLocations =
        listOf(
            Triple(stringResource(R.string.fridge), R.drawable.fridge, fridgeItems),
            Triple(stringResource(R.string.freezer), R.drawable.freezer, freezerItems),
            Triple(stringResource(R.string.cellar), R.drawable.cellar, cellarItems),
            Triple(stringResource(R.string.pantry), R.drawable.pantry, pantryItems),
        )

    StoryTemplate(
        headline = R.string.check_current_inventory,
        content = {
            Column(
                modifier = Modifier.padding(horizontal = 30.dp),
            ) {
                storageLocations.forEach { (title, image, items) ->
                    StorageLocation(
                        title = title,
                        image = painterResource(id = image),
                        items = items,
                        editProductSheetState = editProductSheetState,
                        onItemClick = { },
                        isClickable = false,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        },
    )
}

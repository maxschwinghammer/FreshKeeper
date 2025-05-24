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
import com.freshkeeper.model.Category
import com.freshkeeper.model.FoodItem
import com.freshkeeper.model.FoodStatus
import com.freshkeeper.model.StorageLocation
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
                storageLocation = StorageLocation.FRIDGE,
                userId = "",
                householdId = "",
                expiryTimestamp = 0L,
                quantity = 1,
                unit = "",
                category = Category.DAIRY_GOODS,
                status = FoodStatus.ACTIVE,
                daysDifference = 10,
            ),
            FoodItem(
                name = stringResource(R.string.food_serrano_ham),
                storageLocation = StorageLocation.FRIDGE,
                userId = "",
                householdId = "",
                expiryTimestamp = 4047353600000,
                quantity = 1,
                unit = "",
                category = Category.MEAT,
                status = FoodStatus.ACTIVE,
                daysDifference = 10,
            ),
            FoodItem(
                name = stringResource(R.string.food_mozzarella),
                storageLocation = StorageLocation.FRIDGE,
                userId = "",
                householdId = "",
                expiryTimestamp = 0L,
                quantity = 1,
                unit = "",
                category = Category.DAIRY_GOODS,
                status = FoodStatus.ACTIVE,
                daysDifference = 10,
            ),
            FoodItem(
                name = stringResource(R.string.food_yogurt),
                storageLocation = StorageLocation.FRIDGE,
                userId = "",
                householdId = "",
                expiryTimestamp = 4047353600000,
                quantity = 1,
                unit = "",
                category = Category.DAIRY_GOODS,
                status = FoodStatus.ACTIVE,
                daysDifference = 10,
            ),
            FoodItem(
                name = stringResource(R.string.food_hummus),
                storageLocation = StorageLocation.FRIDGE,
                userId = "",
                householdId = "",
                expiryTimestamp = 4047353600000,
                quantity = 1,
                unit = "",
                category = Category.VEGETABLES,
                status = FoodStatus.ACTIVE,
                daysDifference = 10,
            ),
        )

    val freezerItems =
        listOf(
            FoodItem(
                name = stringResource(R.string.food_edamame),
                storageLocation = StorageLocation.FREEZER,
                userId = "",
                householdId = "",
                expiryTimestamp = 4047353600000,
                quantity = 1,
                unit = "",
                category = Category.VEGETABLES,
                status = FoodStatus.ACTIVE,
                daysDifference = 10,
            ),
            FoodItem(
                name = stringResource(R.string.food_falafel),
                storageLocation = StorageLocation.FREEZER,
                userId = "",
                householdId = "",
                expiryTimestamp = 4047353600000,
                quantity = 1,
                unit = "",
                category = Category.VEGETABLES,
                status = FoodStatus.ACTIVE,
                daysDifference = 10,
            ),
            FoodItem(
                name = stringResource(R.string.food_spinach),
                storageLocation = StorageLocation.FREEZER,
                userId = "",
                householdId = "",
                expiryTimestamp = 4047353600000,
                quantity = 1,
                unit = "",
                category = Category.VEGETABLES,
                status = FoodStatus.ACTIVE,
                daysDifference = 10,
            ),
            FoodItem(
                name = stringResource(R.string.food_gyoza),
                storageLocation = StorageLocation.FREEZER,
                userId = "",
                householdId = "",
                expiryTimestamp = 0L,
                quantity = 1,
                unit = "",
                category = Category.OTHER,
                status = FoodStatus.ACTIVE,
                daysDifference = 10,
            ),
            FoodItem(
                name = stringResource(R.string.food_ice),
                storageLocation = StorageLocation.FREEZER,
                userId = "",
                householdId = "",
                expiryTimestamp = 0L,
                quantity = 1,
                unit = "",
                category = Category.OTHER,
                status = FoodStatus.ACTIVE,
                daysDifference = 10,
            ),
        )

    val cellarItems =
        listOf(
            FoodItem(
                name = stringResource(R.string.food_pickled_vegetables),
                storageLocation = StorageLocation.CELLAR,
                userId = "",
                householdId = "",
                expiryTimestamp = 4047353600000,
                quantity = 1,
                unit = "",
                category = Category.VEGETABLES,
                status = FoodStatus.ACTIVE,
                daysDifference = 10,
            ),
            FoodItem(
                name = stringResource(R.string.food_onions),
                storageLocation = StorageLocation.CELLAR,
                userId = "",
                householdId = "",
                expiryTimestamp = 4047353600000,
                quantity = 2,
                unit = "",
                category = Category.VEGETABLES,
                status = FoodStatus.ACTIVE,
                daysDifference = 10,
            ),
            FoodItem(
                name = stringResource(R.string.food_wine),
                storageLocation = StorageLocation.CELLAR,
                userId = "",
                householdId = "",
                expiryTimestamp = 0L,
                quantity = 1,
                unit = "",
                category = Category.DRINKS,
                status = FoodStatus.ACTIVE,
                daysDifference = 10,
            ),
        )

    val pantryItems =
        listOf(
            FoodItem(
                name = stringResource(R.string.food_coconut_milk),
                storageLocation = StorageLocation.PANTRY,
                userId = "",
                householdId = "",
                expiryTimestamp = 4047353600000,
                quantity = 1,
                unit = "",
                category = Category.CANNED_GOODS,
                status = FoodStatus.ACTIVE,
                daysDifference = 10,
            ),
            FoodItem(
                name = stringResource(R.string.food_spaghetti),
                storageLocation = StorageLocation.PANTRY,
                userId = "",
                householdId = "",
                expiryTimestamp = 4047353600000,
                quantity = 1,
                unit = "",
                category = Category.PASTA,
                status = FoodStatus.ACTIVE,
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

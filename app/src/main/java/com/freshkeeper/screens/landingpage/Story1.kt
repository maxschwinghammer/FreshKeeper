package com.freshkeeper.screens.landingpage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.freshkeeper.R
import com.freshkeeper.model.FoodItem
import com.freshkeeper.model.FoodStatus
import com.freshkeeper.screens.home.FoodList
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Suppress("ktlint:standard:function-naming")
@Composable
fun Story1() {
    fun daysFromNow(days: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, days)
        return calendar.timeInMillis
    }
    val expiringSoonItems =
        listOf(
            FoodItem(
                id = "3",
                userId = "user1",
                householdId = "household1",
                name = stringResource(R.string.food_whole_grain_bread),
                expiryTimestamp = daysFromNow(1),
                daysDifference = 1,
                quantity = 1,
                unit = "",
                storageLocation = "",
                category = "",
                status = FoodStatus.ACTIVE,
            ),
            FoodItem(
                id = "5",
                userId = "user1",
                householdId = "household1",
                name = stringResource(R.string.food_lettuce),
                expiryTimestamp = daysFromNow(1),
                daysDifference = 1,
                quantity = 150,
                unit = "",
                storageLocation = "",
                category = "",
                status = FoodStatus.ACTIVE,
            ),
            FoodItem(
                id = "4",
                userId = "user1",
                householdId = "household1",
                name = stringResource(R.string.food_yogurt),
                expiryTimestamp = daysFromNow(2),
                daysDifference = 2,
                quantity = 500,
                unit = "",
                storageLocation = "",
                category = "",
                status = FoodStatus.ACTIVE,
            ),
            FoodItem(
                id = "1",
                userId = "user1",
                householdId = "household1",
                name = stringResource(R.string.food_minced_meat),
                expiryTimestamp = daysFromNow(7),
                daysDifference = 7,
                quantity = 1,
                unit = "",
                storageLocation = "",
                category = "",
                status = FoodStatus.ACTIVE,
            ),
            FoodItem(
                id = "2",
                userId = "user1",
                householdId = "household1",
                name = stringResource(R.string.food_eggs),
                expiryTimestamp = daysFromNow(9),
                daysDifference = 9,
                quantity = 10,
                unit = "",
                storageLocation = "",
                category = "",
                status = FoodStatus.ACTIVE,
            ),
        )

    val expiredItems =
        listOf(
            FoodItem(
                id = "10",
                userId = "user1",
                householdId = "household1",
                name = stringResource(R.string.food_milk),
                expiryTimestamp = daysFromNow(-1),
                daysDifference = -1,
                quantity = 150,
                unit = "",
                storageLocation = "",
                category = "",
                status = FoodStatus.THROWN_AWAY,
            ),
            FoodItem(
                id = "8",
                userId = "user1",
                householdId = "household1",
                name = stringResource(R.string.food_tofu),
                expiryTimestamp = daysFromNow(-1),
                daysDifference = -1,
                quantity = 1,
                unit = "",
                storageLocation = "",
                category = "",
                status = FoodStatus.CONSUMED,
            ),
            FoodItem(
                id = "6",
                userId = "user1",
                householdId = "household1",
                name = stringResource(R.string.food_skyr),
                expiryTimestamp = daysFromNow(-2),
                daysDifference = -2,
                quantity = 1,
                unit = "",
                storageLocation = "",
                category = "",
                status = FoodStatus.THROWN_AWAY,
            ),
            FoodItem(
                id = "9",
                userId = "user1",
                householdId = "household1",
                name = stringResource(R.string.food_camembert),
                expiryTimestamp = daysFromNow(-12),
                daysDifference = -12,
                quantity = 500,
                unit = "",
                storageLocation = "",
                category = "",
                status = FoodStatus.THROWN_AWAY,
            ),
            FoodItem(
                id = "7",
                userId = "user1",
                householdId = "household1",
                name = stringResource(R.string.food_cream),
                expiryTimestamp = daysFromNow(-25),
                daysDifference = -25,
                quantity = 10,
                unit = "",
                storageLocation = "",
                category = "",
                status = FoodStatus.THROWN_AWAY,
            ),
        )

    val listState = rememberLazyListState()
    val editProductSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    StoryTemplate(
        headline = R.string.overview_expiring_products,
        content = {
            LazyColumn(
                state = listState,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 30.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item {
                    FoodList(
                        title = stringResource(id = R.string.expiring_soon),
                        image = painterResource(id = R.drawable.expiring_soon),
                        items =
                            expiringSoonItems.map { item ->
                                val displayText =
                                    when {
                                        item.daysDifference == 1 ->
                                            stringResource(R.string.tomorrow)

                                        item.daysDifference > 1 ->
                                            stringResource(
                                                R.string.in_days,
                                                item.daysDifference,
                                            )

                                        item.daysDifference == 0 ->
                                            stringResource(R.string.today)

                                        else -> ""
                                    }
                                Triple(item.id, item.name, displayText)
                            },
                        editProductSheetState = editProductSheetState,
                        onEditProduct = {},
                        isClickable = false,
                    )
                }
                item {}
                item {
                    FoodList(
                        title = stringResource(R.string.expired),
                        image = painterResource(id = R.drawable.warning),
                        items =
                            expiredItems.map { item ->
                                val displayText =
                                    when {
                                        -item.daysDifference == 1 ->
                                            stringResource(
                                                R.string.yesterday,
                                            )

                                        item.daysDifference < 0 ->
                                            stringResource(
                                                R.string.days_ago,
                                                -item.daysDifference,
                                            )

                                        else -> ""
                                    }
                                Triple(item.id, item.name, displayText)
                            },
                        editProductSheetState = editProductSheetState,
                        onEditProduct = {},
                        isClickable = false,
                    )
                }
            }
        },
    )
}

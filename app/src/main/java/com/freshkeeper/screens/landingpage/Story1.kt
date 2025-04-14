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
                id = "1",
                userId = "user1",
                householdId = "household1",
                name = "Hackfleisch",
                expiryTimestamp = daysFromNow(2),
                quantity = 1,
                unit = "Liter",
                storageLocation = "Kühlschrank",
                category = "Milchprodukte",
                consumed = false,
                thrownAway = false,
            ),
            FoodItem(
                id = "2",
                userId = "user1",
                householdId = "household1",
                name = "Eier",
                expiryTimestamp = daysFromNow(3),
                quantity = 10,
                unit = "Stück",
                storageLocation = "Kühlschrank",
                category = "Frischeware",
                consumed = false,
                thrownAway = false,
            ),
            FoodItem(
                id = "3",
                userId = "user1",
                householdId = "household1",
                name = "Vollkornbrot",
                expiryTimestamp = daysFromNow(1),
                quantity = 1,
                unit = "Laib",
                storageLocation = "Brotbox",
                category = "Backwaren",
                consumed = false,
                thrownAway = false,
            ),
            FoodItem(
                id = "4",
                userId = "user1",
                householdId = "household1",
                name = "Joghurt",
                expiryTimestamp = daysFromNow(2),
                quantity = 500,
                unit = "g",
                storageLocation = "Kühlschrank",
                category = "Milchprodukte",
                consumed = false,
                thrownAway = false,
            ),
            FoodItem(
                id = "5",
                userId = "user1",
                householdId = "household1",
                name = "Salat",
                expiryTimestamp = daysFromNow(1),
                quantity = 150,
                unit = "g",
                storageLocation = "Kühlschrank",
                category = "Frischeware",
                consumed = false,
                thrownAway = false,
            ),
        )

    val expiredItems =
        listOf(
            FoodItem(
                id = "6",
                userId = "user1",
                householdId = "household1",
                name = "Skyr",
                expiryTimestamp = daysFromNow(-2),
                quantity = 1,
                unit = "Liter",
                storageLocation = "Kühlschrank",
                category = "Milchprodukte",
                consumed = false,
                thrownAway = true,
            ),
            FoodItem(
                id = "7",
                userId = "user1",
                householdId = "household1",
                name = "Sahne",
                expiryTimestamp = daysFromNow(-3),
                quantity = 10,
                unit = "Stück",
                storageLocation = "Kühlschrank",
                category = "Frischeware",
                consumed = false,
                thrownAway = true,
            ),
            FoodItem(
                id = "8",
                userId = "user1",
                householdId = "household1",
                name = "Tofu",
                expiryTimestamp = daysFromNow(-1),
                quantity = 1,
                unit = "Laib",
                storageLocation = "Brotbox",
                category = "Backwaren",
                consumed = true,
                thrownAway = false,
            ),
            FoodItem(
                id = "9",
                userId = "user1",
                householdId = "household1",
                name = "Camembert",
                expiryTimestamp = daysFromNow(-2),
                quantity = 500,
                unit = "g",
                storageLocation = "Kühlschrank",
                category = "Milchprodukte",
                consumed = false,
                thrownAway = true,
            ),
            FoodItem(
                id = "10",
                userId = "user1",
                householdId = "household1",
                name = "Milch",
                expiryTimestamp = daysFromNow(-1),
                quantity = 150,
                unit = "g",
                storageLocation = "Kühlschrank",
                category = "Frischeware",
                consumed = false,
                thrownAway = true,
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
                        .padding(horizontal = 15.dp),
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

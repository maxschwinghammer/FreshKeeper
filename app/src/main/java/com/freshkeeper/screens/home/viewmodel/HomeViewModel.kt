package com.freshkeeper.screens.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.freshkeeper.R
import java.time.Instant
import java.time.temporal.ChronoUnit

class HomeViewModel : ViewModel() {
    private val _expiringSoonItems = MutableLiveData<List<FoodItem>>()
    val expiringSoonItems: LiveData<List<FoodItem>> = _expiringSoonItems

    private val _expiredItems = MutableLiveData<List<FoodItem>>()
    val expiredItems: LiveData<List<FoodItem>> = _expiredItems

    init {
        val items =
            listOf(
                FoodItem(
                    1,
                    "Chicken strips",
                    1734050600000,
                    500,
                    "g",
                    R.string.freezer,
                    R.string.meat,
                    isConsumed = false,
                    isThrownAway = false,
                    "",
                ),
                FoodItem(
                    2,
                    "Mozzarella",
                    1734020600000,
                    500,
                    "g",
                    R.string.freezer,
                    R.string.dairy_goods,
                    isConsumed = false,
                    isThrownAway = false,
                    "",
                ),
                FoodItem(
                    3,
                    "Avocado",
                    1731687800000,
                    500,
                    "g",
                    R.string.freezer,
                    R.string.vegetables,
                    isConsumed = false,
                    isThrownAway = false,
                    "",
                ),
                FoodItem(
                    4,
                    "Minced meat",
                    1733156600000,
                    500,
                    "g",
                    R.string.freezer,
                    R.string.meat,
                    isConsumed = false,
                    isThrownAway = false,
                    "",
                ),
                FoodItem(
                    5,
                    "Toast",
                    1731687800000,
                    500,
                    "g",
                    R.string.bread_box,
                    R.string.bread,
                    isConsumed = false,
                    isThrownAway = false,
                    "",
                ),
                FoodItem(
                    6,
                    "Milk",
                    1733070200000,
                    500,
                    "g",
                    R.string.freezer,
                    R.string.dairy_goods,
                    isConsumed = false,
                    isThrownAway = false,
                    "",
                ),
                FoodItem(
                    7,
                    "Bacon",
                    1731687800000,
                    500,
                    "g",
                    R.string.freezer,
                    R.string.meat,
                    isConsumed = false,
                    isThrownAway = false,
                    "",
                ),
                FoodItem(
                    8,
                    "Feta cheese",
                    1733329400000,
                    500,
                    "g",
                    R.string.freezer,
                    R.string.dairy_goods,
                    isConsumed = false,
                    isThrownAway = false,
                    "",
                ),
                FoodItem(
                    9,
                    "Cream",
                    1733415800000,
                    500,
                    "g",
                    R.string.freezer,
                    R.string.dairy_goods,
                    isConsumed = false,
                    isThrownAway = false,
                    "",
                ),
                FoodItem(
                    10,
                    "Organic strawberries with a long name",
                    1733502200000,
                    500,
                    "g",
                    R.string.freezer,
                    R.string.fruits,
                    isConsumed = false,
                    isThrownAway = false,
                    "",
                ),
            )

        val currentTimestamp = Instant.now().toEpochMilli()

        val categorizedItems =
            items.map { item ->
                val daysDifference =
                    ChronoUnit.DAYS
                        .between(
                            Instant.ofEpochMilli(currentTimestamp),
                            Instant.ofEpochMilli(item.expiryTimestamp),
                        ).toInt()

                item.copy(daysDifference = daysDifference)
            }

        _expiringSoonItems.value =
            categorizedItems.filter { it.daysDifference >= 0 }.sortedBy {
                it.expiryTimestamp
            }

        _expiredItems.value =
            categorizedItems.filter { it.daysDifference < 0 }.sortedByDescending {
                it.expiryTimestamp
            }
    }
}

package com.freshkeeper.screens.household.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.freshkeeper.R
import com.freshkeeper.screens.household.Activity
import com.freshkeeper.screens.household.Member

class HouseholdViewModel : ViewModel() {
    private val _mostWastedItems = MutableLiveData<List<Pair<String, String>>>()
    private val _activities = MutableLiveData<List<Activity>?>()
    val activities: MutableLiveData<List<Activity>?> = _activities
    val mostWastedItems: MutableLiveData<List<Pair<String, String>>> = _mostWastedItems

    init {
        _mostWastedItems.value =
            listOf(
                "Milk" to "4 items",
                "Bread" to "3 items",
                "Apples" to "2 items",
                "Yogurt" to "1 item",
                "Lettuce" to "1 item",
            )
        _activities.value =
            listOf(
                Activity(
                    "add_product",
                    "Emily added 'Spaghetti' to the cupboard",
                ),
                Activity(
                    "edit",
                    "Oliver changed the household name to 'Smith Family'",
                ),
                Activity(
                    "user_joined",
                    "Sophia has joined your household",
                ),
                Activity(
                    "remove",
                    "Oliver marked 'Tomatoes' as consumed",
                ),
                Activity(
                    "add_location",
                    "James added a new storage location 'Freezer'",
                ),
                Activity(
                    "update_quantity",
                    "Emily updated the quantity of 'Avocado' in the fridge",
                ),
            )
    }

    fun removeActivity(activity: Activity) {
        val updatedList = _activities.value?.toMutableList()
        updatedList?.remove(activity)
        _activities.value = updatedList
    }

    private val _members = MutableLiveData<List<Member>?>()
    val members: MutableLiveData<List<Member>?> = _members

    init {
        _members.value =
            listOf(
                Member(imageId = R.drawable.example_avatar_1, name = "Alice"),
                Member(imageId = R.drawable.example_avatar_2, name = "Bob"),
                Member(imageId = R.drawable.example_avatar_3, name = "Charlie"),
                Member(imageId = R.drawable.example_avatar_4, name = "Dennis"),
            )
    }
}

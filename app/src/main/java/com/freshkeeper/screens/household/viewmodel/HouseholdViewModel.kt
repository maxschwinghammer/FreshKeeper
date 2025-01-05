package com.freshkeeper.screens.household.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.freshkeeper.R
import com.freshkeeper.screens.home.viewmodel.FoodItem
import com.freshkeeper.screens.household.Activity
import com.freshkeeper.screens.household.Member
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class HouseholdViewModel : ViewModel() {
    private val _members = MutableLiveData<List<Member>?>()
    val members: MutableLiveData<List<Member>?> = _members

    private val _activities = MutableLiveData<List<Activity>?>()
    val activities: MutableLiveData<List<Activity>?> = _activities

    private val _totalFoodWaste = MutableLiveData<Int>()
    val totalFoodWaste: LiveData<Int> = _totalFoodWaste

    private val _averageFoodWastePerDay = MutableLiveData<Float>()
    val averageFoodWastePerDay: LiveData<Float> = _averageFoodWastePerDay

    private val _daysWithNoWaste = MutableLiveData<Int>()
    val daysWithNoWaste: LiveData<Int> = _daysWithNoWaste

    private val _mostWastedItems = MutableLiveData<List<Pair<String, String>>>()
    val mostWastedItems: LiveData<List<Pair<String, String>>> = _mostWastedItems

    private val firestore = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    init {
        _members.value =
            listOf(
                Member(imageId = R.drawable.example_avatar_1, name = "Alice"),
                Member(imageId = R.drawable.example_avatar_2, name = "Bob"),
                Member(imageId = R.drawable.example_avatar_3, name = "Charlie"),
                Member(imageId = R.drawable.example_avatar_4, name = "Dennis"),
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
        loadFoodWasteData()
    }

    private fun loadFoodWasteData() {
        if (userId == null) return

        val currentDate = Instant.now()
        val currentYear = currentDate.atZone(ZoneId.systemDefault()).year
        val currentMonth = currentDate.atZone(ZoneId.systemDefault()).monthValue

        firestore
            .collection("foodItems")
            .whereEqualTo("userId", userId)
            .whereEqualTo("thrownAway", true)
            .get()
            .addOnSuccessListener { documents ->
                val totalWaste = documents.size()
                _totalFoodWaste.value = totalWaste

                val daysInMonth = currentDate.atZone(ZoneId.systemDefault()).toLocalDate().lengthOfMonth()
                val averageWastePerDay = if (daysInMonth > 0) totalWaste.toFloat() / daysInMonth else 0f
                _averageFoodWastePerDay.value = averageWastePerDay

                val daysWithoutWaste = calculateDaysWithoutWaste(currentYear, currentMonth)
                _daysWithNoWaste.value = daysWithoutWaste

                calculateMostWastedItems(documents)
            }.addOnFailureListener {
                Log.e("HouseholdViewModel", "Error getting documents: ", it)
            }
    }

    private fun calculateMostWastedItems(documents: QuerySnapshot) {
        val foodCountMap = mutableMapOf<String, Int>()

        documents.forEach { doc ->
            val foodItem = doc.toObject(FoodItem::class.java)
            foodItem.let {
                val itemName = it.name
                foodCountMap[itemName] = foodCountMap.getOrDefault(itemName, 0) + 1
            }
        }
        val sortedFoodItems =
            foodCountMap.entries
                .sortedByDescending { it.value }
                .map { it.key to it.value.toString() }

        _mostWastedItems.value = sortedFoodItems.take(5)
    }

    private fun calculateDaysWithoutWaste(
        year: Int,
        month: Int,
    ): Int {
        var daysWithoutWaste = 0

        val daysInMonth = LocalDate.of(year, month, 1).lengthOfMonth()

        for (day in 1..daysInMonth) {
            val dayStart = LocalDate.of(year, month, day).atStartOfDay()
            val dayEnd = dayStart.plusDays(1)

            firestore
                .collection("foodItems")
                .whereEqualTo("userId", userId)
                .whereEqualTo("thrownAway", true)
                .whereGreaterThanOrEqualTo(
                    "timestamp",
                    dayStart.atZone(ZoneId.systemDefault()).toInstant(),
                ).whereLessThan("timestamp", dayEnd.atZone(ZoneId.systemDefault()).toInstant())
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        daysWithoutWaste++
                    }
                }.addOnFailureListener {
                    Log.e("HouseholdViewModel", "Error getting documents: ", it)
                }
        }

        return daysWithoutWaste
    }

    fun removeActivity(activity: Activity) {
        val updatedList = _activities.value?.toMutableList()
        updatedList?.remove(activity)
        _activities.value = updatedList
    }
}

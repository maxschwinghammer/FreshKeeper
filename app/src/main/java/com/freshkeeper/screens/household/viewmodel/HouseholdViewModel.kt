package com.freshkeeper.screens.household.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.freshkeeper.model.Activity
import com.freshkeeper.model.FoodItem
import com.freshkeeper.model.Household
import com.freshkeeper.model.Member
import com.freshkeeper.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class HouseholdViewModel
    @Inject
    constructor(
        private val firestore: FirebaseFirestore,
    ) : ViewModel() {
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

        private val _isInHousehold = MutableLiveData<Boolean>(false)
        val isInHousehold: LiveData<Boolean> = _isInHousehold

        private val userId = FirebaseAuth.getInstance().currentUser?.uid

        init {
            loadMembers()
            loadActivities()
            loadFoodWasteData()
        }

        private fun loadMembers() {
            if (userId == null) return

            firestore
                .collection("households")
                .whereArrayContains("users", userId)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        _isInHousehold.value = false
                        return@addOnSuccessListener
                    }

                    _isInHousehold.value = true
                    val household = documents.documents.first().toObject(Household::class.java)

                    household?.users?.let { userIds ->
                        loadUserDetails(userIds)
                    }
                }.addOnFailureListener { e ->
                    Log.e("HouseholdViewModel", "Error getting household: ", e)
                }
        }

        private fun loadUserDetails(userIds: List<String>) {
            firestore
                .collection("users")
                .whereIn("id", userIds)
                .get()
                .addOnSuccessListener { documents ->
                    val membersList = mutableListOf<Member>()
                    documents.forEach { document ->
                        val user = document.toObject(User::class.java)
                        val profilePictureBase64 = user.profilePicture

                        membersList.add(
                            Member(
                                profilePictureBase64 = profilePictureBase64,
                                name = user.displayName ?: "Unknown",
                            ),
                        )
                    }
                    _members.value = membersList
                }.addOnFailureListener { e ->
                    Log.e("HouseholdViewModel", "Error getting user details: ", e)
                }
        }

        private fun loadActivities() {
            if (userId == null) return

            firestore
                .collection("activities")
                .whereEqualTo("userId", userId)
                .whereEqualTo("deleted", false)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnSuccessListener { documents ->
                    val activitiesList = mutableListOf<Activity>()
                    documents.forEach { document ->
                        val activity = document.toObject(Activity::class.java)
                        activitiesList.add(activity)
                    }
                    _activities.value = activitiesList
                }.addOnFailureListener { e ->
                    Log.e("HouseholdViewModel", "Error getting activities: ", e)
                }
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
            val activityRef =
                activity.id?.let { firestore.collection("activities").document(it) }

            activityRef
                ?.delete()
                ?.addOnSuccessListener {
                    val updatedList = _activities.value?.toMutableList()
                    updatedList?.remove(activity)
                    _activities.value = updatedList
                }?.addOnFailureListener { e ->
                    Log.w("Firestore", "Error deleting activity", e)
                }
        }
    }

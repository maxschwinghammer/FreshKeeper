package com.freshkeeper.screens.household.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.freshkeeper.model.Activity
import com.freshkeeper.model.FoodItem
import com.freshkeeper.model.Household
import com.freshkeeper.model.Member
import com.freshkeeper.model.ProfilePicture
import com.freshkeeper.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import java.time.YearMonth
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

        private val _isInHousehold = MutableLiveData(false)
        val isInHousehold: LiveData<Boolean> = _isInHousehold

        private val userId = FirebaseAuth.getInstance().currentUser?.uid

        private val _householdId = MutableLiveData<String?>()
        val householdId: LiveData<String?> = _householdId

        init {
            loadHouseholdId()
        }

        private fun loadHouseholdId() {
            if (userId == null) return

            firestore
                .collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    _householdId.value = document.getString("householdId")
                    loadMembers()
                    loadActivities()
                    loadFoodWasteData()
                }.addOnFailureListener {
                    Log.e("HouseholdViewModel", "Error loading householdId from Firestore")
                }
        }

        private fun loadMembers() {
            if (userId == null) return

            firestore
                .collection("households")
                .whereArrayContains("users", userId)
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        Log.e("HouseholdViewModel", "Error listening for household changes: ", e)
                        return@addSnapshotListener
                    }

                    if (snapshots == null || snapshots.isEmpty) {
                        _isInHousehold.value = false
                        _members.value = null
                        return@addSnapshotListener
                    }

                    _isInHousehold.value = true
                    val household = snapshots.documents.first().toObject(Household::class.java)
                    _householdId.value = snapshots.documents.first().id

                    household?.users?.let { userIds ->
                        if (_members.value == null || hasUserListChanged(userIds)) {
                            loadUserDetails(userIds)
                        }
                    }
                }
        }

        private fun hasUserListChanged(newUsers: List<User?>): Boolean {
            val cachedUserIds = _members.value?.map { it.userId } ?: return true
            val newUserIds = newUsers.mapNotNull { it?.id }
            return cachedUserIds.size != newUserIds.size || !cachedUserIds.containsAll(newUserIds)
        }

        private fun loadUserDetails(userIds: List<User?>) {
            firestore
                .collection("users")
                .whereIn("id", userIds)
                .get()
                .addOnSuccessListener { documents ->
                    val membersList = mutableListOf<Member>()
                    var loadedCount = 0

                    documents.forEach { document ->
                        val user = document.toObject(User::class.java)
                        val profilePictureId = user.profilePicture

                        if (!profilePictureId.isNullOrEmpty()) {
                            firestore
                                .collection("profilePictures")
                                .document(profilePictureId)
                                .get()
                                .addOnSuccessListener { profilePictureDoc ->
                                    val profilePicture = profilePictureDoc.toObject(ProfilePicture::class.java)
                                    membersList.add(
                                        Member(
                                            profilePicture = profilePicture,
                                            name = user.displayName ?: "Unknown",
                                            userId = user.id,
                                        ),
                                    )
                                    loadedCount++
                                    if (loadedCount == documents.size()) {
                                        _members.value = membersList
                                    }
                                }.addOnFailureListener { e ->
                                    Log.e("HouseholdViewModel", "Error getting profile picture: ", e)
                                    membersList.add(
                                        Member(
                                            profilePicture = null,
                                            name = user.displayName ?: "Unknown",
                                            userId = user.id,
                                        ),
                                    )
                                    loadedCount++
                                    if (loadedCount == documents.size()) {
                                        _members.value = membersList
                                    }
                                }
                        } else {
                            membersList.add(
                                Member(
                                    profilePicture = null,
                                    name = user.displayName ?: "Unknown",
                                    userId = user.id,
                                ),
                            )
                            loadedCount++
                            if (loadedCount == documents.size()) {
                                _members.value = membersList
                            }
                        }
                    }
                }.addOnFailureListener { e ->
                    Log.e("HouseholdViewModel", "Error getting user details: ", e)
                }
        }

        private fun loadActivities() {
            val query =
                if (_householdId.value != null) {
                    firestore
                        .collection("activities")
                        .whereEqualTo("householdId", _householdId.value)
                } else {
                    firestore
                        .collection("activities")
                        .whereEqualTo("userId", userId)
                }

            query
                .whereEqualTo("deleted", false)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnSuccessListener { documents ->
                    val activitiesList = documents.map { it.toObject(Activity::class.java) }
                    _activities.value = activitiesList
                }.addOnFailureListener { e ->
                    Log.e("HouseholdViewModel", "Error getting activities: ", e)
                }
        }

        private fun loadFoodWasteData() {
            val currentDate = Instant.now()
            val currentYear = currentDate.atZone(ZoneId.systemDefault()).year
            val currentMonth = currentDate.atZone(ZoneId.systemDefault()).monthValue

            val query =
                if (_householdId.value != null) {
                    firestore
                        .collection("foodItems")
                        .whereEqualTo("householdId", _householdId.value)
                } else {
                    firestore
                        .collection("foodItems")
                        .whereEqualTo("userId", userId)
                }

            query
                .whereEqualTo("thrownAway", true)
                .get()
                .addOnSuccessListener { documents ->
                    val totalWaste = documents.size()
                    _totalFoodWaste.value = totalWaste

                    val daysInMonth = currentDate.atZone(ZoneId.systemDefault()).toLocalDate().lengthOfMonth()
                    val averageWastePerDay = if (daysInMonth > 0) totalWaste.toFloat() / daysInMonth else 0f
                    _averageFoodWastePerDay.value = averageWastePerDay

                    calculateDaysWithoutWaste(currentYear, currentMonth) { daysWithoutWaste ->
                        _daysWithNoWaste.value = daysWithoutWaste
                    }

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
            currentYear: Int,
            currentMonth: Int,
            onResult: (Int) -> Unit,
        ) {
            val query =
                if (_householdId.value != null) {
                    firestore
                        .collection("foodItems")
                        .whereEqualTo("householdId", _householdId.value)
                } else {
                    firestore
                        .collection("foodItems")
                        .whereEqualTo("userId", userId)
                }

            query
                .whereEqualTo("thrownAway", true)
                .get()
                .addOnSuccessListener { documents ->
                    val daysWithWaste =
                        documents
                            .mapNotNull { doc ->
                                doc.getTimestamp("throwAwayDate")?.toDate()?.toInstant()
                            }.filter { date ->
                                val dateTime = date.atZone(ZoneId.systemDefault()).toLocalDate()
                                dateTime.year == currentYear && dateTime.monthValue == currentMonth
                            }.map { it.atZone(ZoneId.systemDefault()).toLocalDate() }
                            .distinct()
                            .size

                    val totalDaysInMonth = YearMonth.of(currentYear, currentMonth).lengthOfMonth()
                    val daysWithoutWaste = totalDaysInMonth - daysWithWaste

                    onResult(daysWithoutWaste)
                }.addOnFailureListener { e ->
                    Log.e("HouseholdViewModel", "Error calculating days without waste: ", e)
                    onResult(0)
                }
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
                    Log.e("Firestore", "Error deleting activity", e)
                }
        }
    }

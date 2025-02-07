package com.freshkeeper.service

import android.util.Log
import com.freshkeeper.model.Activity
import com.freshkeeper.model.FoodItem
import com.freshkeeper.model.Household
import com.freshkeeper.model.Member
import com.freshkeeper.model.ProfilePicture
import com.freshkeeper.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class HouseholdServiceImpl
    @Inject
    constructor() : HouseholdService {
        private val firestore = FirebaseFirestore.getInstance()
        private val userId = FirebaseAuth.getInstance().currentUser?.uid
        private var householdId: String? = null

        override suspend fun getHousehold(
            onResult: (Household?) -> Unit,
            onFailure: () -> Unit,
        ) {
            if (userId == null) {
                onFailure()
                return
            }

            firestore
                .collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    householdId = document.getString("householdId")
                    if (householdId != null) {
                        firestore
                            .collection("households")
                            .document(householdId!!)
                            .get()
                            .addOnSuccessListener { householdDoc ->
                                val household = householdDoc.toObject(Household::class.java)
                                onResult(household)
                            }.addOnFailureListener { onFailure() }
                    } else {
                        onFailure()
                    }
                }.addOnFailureListener { onFailure() }
        }

        override suspend fun getHouseholdId(
            onResult: (String?) -> Unit,
            onFailure: () -> Unit,
        ) {
            if (userId == null) {
                onFailure()
                return
            }

            firestore
                .collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    householdId = document.getString("householdId")
                    onResult(householdId)
                }.addOnFailureListener { onFailure() }
        }

        override suspend fun getMembers(
            onResult: (List<Member>?) -> Unit,
            onFailure: (Exception) -> Unit,
        ) {
            val coroutineScope = CoroutineScope(Dispatchers.Main)

            householdId?.let {
                firestore
                    .collection("households")
                    .document(it)
                    .get()
                    .addOnSuccessListener { document ->
                        val household = document.toObject(Household::class.java)
                        household?.users?.let { users ->
                            if (users.isNotEmpty()) {
                                coroutineScope.launch {
                                    loadUserDetails(users, coroutineScope, onResult, onFailure)
                                }
                            } else {
                                onFailure(Exception("No users found in household"))
                            }
                        }
                    }.addOnFailureListener {
                        Log.e("HouseholdServiceImpl", "Error loading members")
                    }
            }
        }

        override suspend fun loadUserDetails(
            userIds: List<String>,
            coroutineScope: CoroutineScope,
            onResult: (List<Member>?) -> Unit,
            onFailure: (Exception) -> Unit,
        ) {
            if (userIds.isEmpty()) {
                onResult(emptyList())
                return
            }

            try {
                val documents =
                    firestore
                        .collection("users")
                        .whereIn("id", userIds)
                        .get()
                        .await()

                val membersList = mutableListOf<Member>()

                documents.forEach { document ->
                    val user = document.toObject(User::class.java)
                    val profilePictureId = user.profilePicture

                    val profilePicture =
                        if (!profilePictureId.isNullOrEmpty()) {
                            getProfilePicture(profilePictureId)
                        } else {
                            null
                        }

                    val member =
                        Member(
                            profilePicture = profilePicture,
                            name = user.displayName ?: "Unknown",
                            userId = user.id,
                        )

                    membersList.add(member)
                }

                onResult(membersList)
            } catch (exception: Exception) {
                onFailure(exception)
            }
        }

        override suspend fun getActivities(
            onResult: (List<Activity>?) -> Unit,
            onFailure: (Exception) -> Unit,
        ) {
            val query =
                if (householdId != null) {
                    firestore.collection("activities").whereEqualTo("householdId", householdId)
                } else {
                    firestore.collection("activities").whereEqualTo("userId", userId)
                }

            query
                .whereEqualTo("deleted", false)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnSuccessListener { documents ->
                    val activitiesList = documents.map { it.toObject(Activity::class.java) }
                    onResult(activitiesList)
                }.addOnFailureListener(onFailure)
        }

        override suspend fun getFoodWasteData(
            onResult: (List<FoodItem>) -> Unit,
            onFailure: (Exception) -> Unit,
        ) {
            val query =
                if (householdId != null) {
                    firestore.collection("foodItems").whereEqualTo("householdId", householdId)
                } else {
                    firestore.collection("foodItems").whereEqualTo("userId", userId)
                }

            query
                .whereEqualTo("thrownAway", true)
                .get()
                .addOnSuccessListener { documents ->
                    val foodItems = documents.map { it.toObject(FoodItem::class.java) }
                    onResult(foodItems)
                }.addOnFailureListener(onFailure)
        }

        override suspend fun removeActivity(
            activity: Activity,
            onSuccess: () -> Unit,
            onFailure: (Exception) -> Unit,
        ) {
            val activityRef = activity.id?.let { firestore.collection("activities").document(it) }

            activityRef
                ?.delete()
                ?.addOnSuccessListener { onSuccess() }
                ?.addOnFailureListener(onFailure)
        }

        override suspend fun getProfilePicture(profilePictureId: String): ProfilePicture? {
            val docSnapshot =
                firestore
                    .collection("profilePictures")
                    .document(profilePictureId)
                    .get()
                    .await()

            return if (docSnapshot.exists()) {
                docSnapshot.toObject(ProfilePicture::class.java)
            } else {
                null
            }
        }

        override suspend fun getFoodItems(householdId: String?): List<FoodItem> {
            val query =
                if (householdId != null) {
                    firestore.collection("foodItems").whereEqualTo("householdId", householdId)
                } else {
                    firestore.collection("foodItems").whereEqualTo("userId", userId)
                }

            val documents =
                query
                    .whereEqualTo("consumed", false)
                    .whereEqualTo("thrownAway", false)
                    .get()
                    .await()

            val currentTimestamp = System.currentTimeMillis()
            return documents.mapNotNull { doc ->
                doc.toObject(FoodItem::class.java).apply {
                    daysDifference =
                        ChronoUnit.DAYS
                            .between(
                                Instant.ofEpochMilli(currentTimestamp),
                                Instant.ofEpochMilli(expiryTimestamp),
                            ).toInt()
                }
            }
        }
    }

package com.freshkeeper.model.service

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
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class HouseholdServiceImpl
    @Inject
    constructor(
        private val firestore: FirebaseFirestore,
    ) : HouseholdService {
        private val userId = FirebaseAuth.getInstance().currentUser?.uid

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
                    onResult(document.getString("householdId"))
                }.addOnFailureListener { onFailure() }
        }

        override suspend fun getMembers(
            householdId: String,
            coroutineScope: CoroutineScope,
            onResult: (List<Member>?) -> Unit,
            onFailure: (Exception) -> Unit,
        ) {
            firestore
                .collection("households")
                .document(householdId)
                .get()
                .addOnSuccessListener { document ->
                    val household = document.toObject(Household::class.java)
                    household?.users?.let { userIds ->
                        coroutineScope.launch {
                            loadUserDetails(userIds, coroutineScope, onResult, onFailure)
                        }
                    }
                }.addOnFailureListener(onFailure)
        }

        override suspend fun loadUserDetails(
            userIds: List<User?>,
            coroutineScope: CoroutineScope,
            onResult: (List<Member>?) -> Unit,
            onFailure: (Exception) -> Unit,
        ) {
            firestore
                .collection("users")
                .whereIn("id", userIds)
                .get()
                .addOnSuccessListener { documents ->
                    val membersList = mutableListOf<Member>()
                    var loadedCount = 0
                    val totalCount = documents.size()

                    documents.forEach { document ->
                        val user = document.toObject(User::class.java)
                        val profilePictureId = user.profilePicture

                        coroutineScope.launch {
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
                            loadedCount++

                            if (loadedCount == totalCount) {
                                onResult(membersList)
                            }
                        }
                    }
                }.addOnFailureListener(onFailure)
        }

        override suspend fun getActivities(
            householdId: String?,
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
            householdId: String?,
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

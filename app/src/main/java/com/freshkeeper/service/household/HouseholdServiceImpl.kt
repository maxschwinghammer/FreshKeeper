package com.freshkeeper.service.household

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.freshkeeper.model.Activity
import com.freshkeeper.model.FoodItem
import com.freshkeeper.model.Household
import com.freshkeeper.model.Member
import com.freshkeeper.model.ProfilePicture
import com.freshkeeper.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
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

        override suspend fun getHousehold(onResult: (Household) -> Unit) {
            if (userId == null) {
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
                                if (household != null) {
                                    onResult(household)
                                }
                            }
                    } else {
                        Log.e("HouseholdServiceImpl", "Household ID is null")
                    }
                }
        }

        override suspend fun getHouseholdId(onResult: (String?) -> Unit) {
            if (userId == null) {
                return
            }

            firestore
                .collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    householdId = document.getString("householdId")
                    onResult(householdId)
                }
        }

        override suspend fun updateHouseholdName(newName: String) {
            getHouseholdId(
                onResult = { id ->
                    if (id != null) {
                        CoroutineScope(Dispatchers.IO).launch {
                            firestore
                                .collection("households")
                                .document(id)
                                .update("name", newName)
                                .await()
                        }
                    } else {
                        Log.e("HouseholdServiceImpl", "Household ID is null")
                    }
                },
            )
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
                                    getUserDetails(users, coroutineScope, onResult, onFailure)
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

        override suspend fun getUserDetails(
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

                    val profilePicture =
                        if (!userId.isNullOrEmpty()) {
                            getProfilePicture(userId)
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
                    firestore.collection("activities").whereEqualTo(
                        "householdId",
                        householdId,
                    )
                } else {
                    return
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
                    firestore.collection("foodItems").whereEqualTo(
                        "householdId",
                        householdId,
                    )
                } else {
                    firestore.collection("foodItems").whereEqualTo(
                        "userId",
                        userId,
                    )
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
            val activityRef =
                activity.id?.let {
                    firestore
                        .collection("activities")
                        .document(it)
                }

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
                    firestore.collection("foodItems").whereEqualTo(
                        "householdId",
                        householdId,
                    )
                } else {
                    firestore.collection("foodItems").whereEqualTo(
                        "userId",
                        userId,
                    )
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

        override suspend fun createHousehold(
            name: String,
            type: String,
            onSuccess: (Household) -> Unit,
        ) {
            try {
                val newHousehold =
                    userId?.let {
                        Household(
                            id = firestore.collection("households").document().id,
                            type = type,
                            users = listOf(userId),
                            name = name,
                            createdAt = System.currentTimeMillis(),
                            ownerId = it,
                        )
                    }

                if (newHousehold != null) {
                    firestore
                        .collection("households")
                        .document(newHousehold.id)
                        .set(newHousehold)
                        .await()

                    if (userId != null) {
                        firestore
                            .collection("users")
                            .document(userId)
                            .update("householdId", newHousehold.id)
                            .await()
                    }
                }

                val foodItemsQuerySnapshot =
                    firestore
                        .collection("foodItems")
                        .whereEqualTo("userId", userId)
                        .get()
                        .await()

                val batch = firestore.batch()
                foodItemsQuerySnapshot.documents.forEach { document ->
                    if (newHousehold != null) {
                        batch.update(document.reference, "householdId", newHousehold.id)
                    }
                }
                batch.commit().await()
                if (newHousehold != null) {
                    onSuccess(newHousehold)
                }
            } catch (e: Exception) {
                Log.e("HouseholdService", "Error when creating the household", e)
            }
        }

        override suspend fun leaveHousehold(householdId: String) {
            try {
                firestore
                    .collection("households")
                    .document(householdId)
                    .update("users", FieldValue.arrayRemove(userId))
                    .await()

                if (userId != null) {
                    firestore
                        .collection("users")
                        .document(userId)
                        .update("householdId", null)
                        .await()
                }
            } catch (e: Exception) {
                Log.e("HouseholdService", "Error when leaving the household", e)
            }
        }

        override suspend fun deleteHousehold(
            householdId: String,
            onSuccess: () -> Unit,
        ) {
            try {
                firestore
                    .collection("households")
                    .document(householdId)
                    .delete()
                    .await()

                val usersQuerySnapshot =
                    firestore
                        .collection("users")
                        .whereEqualTo("householdId", householdId)
                        .get()
                        .await()

                val foodItemsQuerySnapshot =
                    firestore
                        .collection("foodItems")
                        .whereEqualTo("householdId", householdId)
                        .get()
                        .await()

                val activitiesQuerySnapshot =
                    firestore
                        .collection("activities")
                        .whereEqualTo("householdId", householdId)
                        .get()
                        .await()

                val batch = firestore.batch()

                usersQuerySnapshot.documents.forEach { document ->
                    batch.update(document.reference, "householdId", null)
                }

                val imageIds = mutableListOf<String>()

                foodItemsQuerySnapshot.documents.forEach { document ->
                    document.getString("imageId")?.let { imageIds.add(it) }
                    batch.delete(document.reference)
                }

                imageIds.forEach { imageId ->
                    batch.delete(
                        firestore
                            .collection("foodItemPictures")
                            .document(imageId),
                    )
                }

                activitiesQuerySnapshot.documents.forEach { document ->
                    batch.delete(document.reference)
                }

                batch.commit().await()
                onSuccess()
            } catch (e: Exception) {
                Log.e(
                    "DeleteHousehold",
                    "Error when deleting the household, updating users, " +
                        "deleting food items, or deleting food item pictures",
                    e,
                )
            }
        }

        override suspend fun deleteProducts() {
            try {
                val foodItemsQuerySnapshot =
                    firestore
                        .collection("foodItems")
                        .whereEqualTo("userId", userId)
                        .get()
                        .await()

                val batch = firestore.batch()

                foodItemsQuerySnapshot.documents.forEach { document ->
                    batch.delete(document.reference)
                }

                batch.commit().await()
            } catch (e: Exception) {
                Log.e("HouseholdService", "Error when deleting food items", e)
            }
        }

        override suspend fun addProducts(householdId: String) {
            try {
                val foodItemsQuerySnapshot =
                    firestore
                        .collection("foodItems")
                        .whereEqualTo("userId", userId)
                        .get()
                        .await()

                val batch = firestore.batch()

                foodItemsQuerySnapshot.documents.forEach { document ->
                    batch.update(document.reference, "householdId", householdId)
                }

                batch.commit().await()
            } catch (e: Exception) {
                Log.e("HouseholdService", "Error when adding products to household", e)
            }
        }

        override suspend fun addUserById(
            userId: String,
            householdId: String,
            context: Context,
            errorText: String,
            successText: String,
            onSuccess: (User) -> Unit,
        ) {
            val userSnapshot =
                firestore
                    .collection("users")
                    .document(userId)
                    .get()
                    .await()

            if (!userSnapshot.exists()) {
                Toast.makeText(context, errorText, Toast.LENGTH_SHORT).show()
                return
            }

            val user = userSnapshot.toObject(User::class.java)

            if (user != null) {
                firestore
                    .collection("households")
                    .document(householdId)
                    .update("users", FieldValue.arrayUnion(user.id))
                    .await()
            }

            firestore
                .collection("users")
                .document(userId)
                .update("householdId", householdId)
                .await()

            if (user != null) {
                onSuccess(user)
            }

            Toast.makeText(context, successText, Toast.LENGTH_SHORT).show()
        }

        override suspend fun joinHouseholdById(
            householdId: String,
            context: Context,
            errorText: String,
            onSuccess: (Household) -> Unit,
        ) {
            try {
                val householdSnapshot =
                    firestore
                        .collection("households")
                        .document(householdId)
                        .get()
                        .await()

                if (!householdSnapshot.exists()) {
                    Toast.makeText(context, errorText, Toast.LENGTH_SHORT).show()
                    return
                }

                firestore
                    .collection("households")
                    .document(householdId)
                    .update("users", FieldValue.arrayUnion(userId))
                    .await()

                if (userId != null) {
                    firestore
                        .collection("users")
                        .document(userId)
                        .update("householdId", householdId)
                        .await()
                }

                val joinedHousehold = householdSnapshot.toObject(Household::class.java)
                if (joinedHousehold != null) {
                    onSuccess(joinedHousehold)
                }
            } catch (e: Exception) {
                Log.e(
                    "HouseholdService",
                    "Error joining household with ID: $householdId",
                    e,
                )
                Toast.makeText(context, errorText, Toast.LENGTH_SHORT).show()
            }
        }

        override suspend fun updateHouseholdType(
            householdId: String,
            ownerId: String,
            newType: String,
            selectedUser: String?,
            users: List<String>,
        ): List<String> =
            try {
                val batch = firestore.batch()
                var updatedUsers = users

                when (newType) {
                    "Single household" -> {
                        val usersToRemove = users.filter { it != ownerId && it != selectedUser }
                        batch.update(
                            firestore.collection("households").document(householdId),
                            "users",
                            FieldValue.arrayRemove(*usersToRemove.toTypedArray()),
                        )

                        usersToRemove.forEach { user ->
                            batch.update(
                                firestore.collection("users").document(user),
                                "householdId",
                                null,
                            )
                        }

                        batch.commit().await()

                        val activitiesSnapshot =
                            firestore
                                .collection("activities")
                                .whereEqualTo("householdId", householdId)
                                .get()
                                .await()

                        val batchDelete = firestore.batch()
                        activitiesSnapshot.documents.forEach { document ->
                            batchDelete.delete(document.reference)
                        }
                        batchDelete.commit().await()

                        updatedUsers = listOfNotNull(users.find { it == ownerId })
                    }

                    "Pair" ->
                        if (selectedUser != null) {
                            val usersToRemove = users.filter { it != ownerId && it != selectedUser }
                            batch.update(
                                firestore.collection("households").document(householdId),
                                "users",
                                FieldValue.arrayRemove(*usersToRemove.toTypedArray()),
                            )

                            usersToRemove.forEach { user ->
                                batch.update(
                                    firestore.collection("users").document(user),
                                    "householdId",
                                    null,
                                )
                            }

                            batch.commit().await()

                            updatedUsers =
                                listOfNotNull(
                                    users.find { it == ownerId },
                                    users.find { it == selectedUser },
                                )
                        }
                }

                firestore
                    .collection("households")
                    .document(householdId)
                    .update("type", newType)
                    .await()

                updatedUsers
            } catch (e: Exception) {
                Log.e("HouseholdService", "Fehler beim Aktualisieren des Haushaltstyps", e)
                users
            }
    }

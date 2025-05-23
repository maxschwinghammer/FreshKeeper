package com.freshkeeper.service.household

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.freshkeeper.R
import com.freshkeeper.model.Activity
import com.freshkeeper.model.Category
import com.freshkeeper.model.EventType
import com.freshkeeper.model.FoodItem
import com.freshkeeper.model.FoodStatus
import com.freshkeeper.model.Household
import com.freshkeeper.model.HouseholdType
import com.freshkeeper.model.ImageType
import com.freshkeeper.model.Member
import com.freshkeeper.model.Picture
import com.freshkeeper.model.Statistics
import com.freshkeeper.model.User
import com.freshkeeper.service.account.AccountService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class HouseholdServiceImpl
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val accountService: AccountService,
    ) : HouseholdService {
        private val firestore = FirebaseFirestore.getInstance()
        private val userId = FirebaseAuth.getInstance().currentUser?.uid

        val coroutineScope = CoroutineScope(Dispatchers.IO)

        var user: User = User()

        init {
            coroutineScope.launch {
                user = accountService.getUserObject()
            }
        }

        override suspend fun getHouseholdId(): String? {
            if (userId != null) {
                val doc =
                    firestore
                        .collection("users")
                        .document(userId)
                        .get()
                        .await()

                return doc.getString("householdId")
            } else {
                return null
            }
        }

        override suspend fun getHousehold(onResult: (Household) -> Unit) {
            if (userId == null) {
                return
            }
            val householdId = getHouseholdId()

            firestore
                .collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (householdId != null) {
                        firestore
                            .collection("households")
                            .document(householdId)
                            .get()
                            .addOnSuccessListener { householdDoc ->
                                val household = householdDoc.toObject(Household::class.java)
                                if (household != null) {
                                    onResult(household)
                                }
                            }
                    }
                }
        }

        override suspend fun updateHouseholdName(newName: String) {
            val householdId = getHouseholdId()
            if (householdId != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    firestore
                        .collection("households")
                        .document(householdId)
                        .update("name", newName)
                        .await()
                }
            } else {
                Log.e("HouseholdServiceImpl", "Household ID is null")
            }
        }

        override suspend fun getMembers(
            onResult: (List<Member>?) -> Unit,
            onFailure: (Exception) -> Unit,
        ) {
            val coroutineScope = CoroutineScope(Dispatchers.Main)
            val householdId = getHouseholdId()

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
                        if (user.id.isNotEmpty()) {
                            getProfilePicture(user.id)
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
            val householdId = getHouseholdId()
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

        override suspend fun getExpiredProducts(): List<FoodItem> {
            val householdId = getHouseholdId()
            return firestore
                .collection("foodItems")
                .whereEqualTo(
                    if (householdId != null) "householdId" else "userId",
                    householdId ?: userId,
                ).whereEqualTo("thrownAway", true)
                .get()
                .await()
                .map { it.toObject(FoodItem::class.java) }
        }

        override suspend fun getAllFoodItems(): List<FoodItem> {
            val householdId = getHouseholdId()
            return firestore
                .collection("foodItems")
                .whereEqualTo(
                    if (householdId != null) "householdId" else "userId",
                    householdId ?: userId,
                ).get()
                .await()
                .map { it.toObject(FoodItem::class.java) }
        }

        override suspend fun calculateStatistics(
            expired: List<FoodItem>,
            allItems: List<FoodItem>,
        ): Statistics {
            val totalWaste = expired.size
            val discardTimestamps = expired.mapNotNull { it.discardTimestamp }
            val averageWaste: Float = totalWaste.toFloat() / 30L

            val periodDays = 30
            val now = System.currentTimeMillis()
            val currentEpochDay = now / 86_400_000L

            val wasteDaysLast30: Set<Long> =
                discardTimestamps
                    .map { it / 86_400_000L }
                    .filter { day ->
                        day in (currentEpochDay - (periodDays - 1))..currentEpochDay
                    }.toSet()

            val daysWithoutWaste: Int =
                (0 until periodDays).count { delta ->
                    val day = currentEpochDay - delta
                    day !in wasteDaysLast30
                }

            val mostWastedItems: List<Pair<FoodItem, Int>> =
                expired
                    .groupingBy { it.name }
                    .eachCount()
                    .entries
                    .sortedByDescending { it.value }
                    .take(3)
                    .mapNotNull { (name, count) ->
                        expired.find { it.name == name }?.let { item ->
                            item to count
                        }
                    }
            val usedItemsPercentage =
                (
                    (
                        allItems
                            .count { it.status != FoodStatus.THROWN_AWAY }
                            .toFloat() / allItems.size.coerceAtLeast(1)
                    ) * 100
                ).toInt()
            val mostWastedCategory =
                expired
                    .groupingBy { it.category }
                    .eachCount()
                    .maxByOrNull { it.value }
                    ?.key
            val discardedDates =
                allItems
                    .filter { it.status == FoodStatus.THROWN_AWAY && it.discardTimestamp != null }
                    .mapNotNull { it.discardTimestamp }

            return Statistics(
                totalWaste = totalWaste,
                averageWaste = averageWaste,
                daysWithoutWaste = daysWithoutWaste,
                mostWastedItems = mostWastedItems,
                usedItemsPercentage = usedItemsPercentage,
                mostWastedCategory = mostWastedCategory ?: Category.OTHER,
                discardedDates = discardedDates,
            )
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

        override suspend fun getProfilePicture(userId: String): Picture? {
            val userDoc =
                firestore
                    .collection("users")
                    .document(userId)
                    .get()
                    .await()

            val map = userDoc.get("profilePicture") as? Map<*, *>
            return if (map != null) {
                Picture(
                    image = map["image"] as? String,
                    type = map["type"] as? ImageType,
                )
            } else {
                null
            }
        }

        override suspend fun getFoodItems(): List<FoodItem> {
            val householdId = getHouseholdId()
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
                    .whereEqualTo("status", FoodStatus.ACTIVE)
                    .get()
                    .await()

            return documents.mapNotNull { doc ->
                doc.toObject(FoodItem::class.java).apply {
                    val currentDate = Instant.ofEpochMilli(System.currentTimeMillis()).atZone(ZoneId.systemDefault()).toLocalDate()
                    val expiryDate = Instant.ofEpochMilli(expiryTimestamp).atZone(ZoneId.systemDefault()).toLocalDate()
                    daysDifference = ChronoUnit.DAYS.between(currentDate, expiryDate).toInt()
                }
            }
        }

        override suspend fun createHousehold(
            name: String,
            type: HouseholdType,
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

                    firestore
                        .collection("users")
                        .document(userId)
                        .update("householdId", newHousehold.id)
                        .await()
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

        override suspend fun leaveHousehold() {
            try {
                val batch = firestore.batch()
                val householdId = getHouseholdId()

                householdId?.let { householdId ->
                    val householdRef =
                        firestore
                            .collection("households")
                            .document(householdId)
                    batch.update(householdRef, "users", FieldValue.arrayRemove(userId))
                }

                userId?.let { userId ->
                    val userRef =
                        firestore
                            .collection("users")
                            .document(userId)
                    batch.update(userRef, "householdId", null)
                }

                userId?.let { userId ->
                    val querySnapshot =
                        firestore
                            .collection("foodItems")
                            .whereEqualTo("userId", userId)
                            .get()
                            .await()
                    for (doc in querySnapshot.documents) {
                        batch.update(doc.reference, "householdId", null)
                    }
                }

                batch.commit().await()
                householdId?.let { logUserActivity(user, it, EventType.USER_LEFT) }
            } catch (e: Exception) {
                Log.e("HouseholdService", "Error when leaving the household", e)
            }
        }

        override suspend fun deleteHousehold(onSuccess: () -> Unit) {
            val householdId = getHouseholdId()
            try {
                householdId?.let {
                    firestore
                        .collection("households")
                        .document(it)
                        .delete()
                        .await()
                }

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

                foodItemsQuerySnapshot.documents.forEach { document ->
                    batch.delete(document.reference)
                }

                activitiesQuerySnapshot.documents.forEach { document ->
                    batch.delete(document.reference)
                }

                batch.commit().await()
                onSuccess()
            } catch (e: Exception) {
                Log.e(
                    "DeleteHousehold",
                    "Error when deleting the household",
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

        override suspend fun addProducts() {
            val householdId = getHouseholdId()
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
            onSuccess: (User) -> Unit,
        ) {
            val householdId = getHouseholdId()
            try {
                val batch = firestore.batch()
                val userRef = firestore.collection("users").document(userId)
                val householdRef = firestore.collection("households").document(householdId ?: "")

                val userSnapshot = userRef.get().await()
                val user =
                    userSnapshot.toObject(User::class.java)
                        ?: throw IllegalArgumentException("User mit ID $userId nicht gefunden")

                batch.update(
                    householdRef,
                    "users",
                    FieldValue.arrayUnion(userId),
                )
                batch.update(
                    userRef,
                    "householdId",
                    householdId,
                )

                batch.commit().await()

                onSuccess(user)
                householdId?.let { logUserActivity(user, it, EventType.USER_ADDED) }

                Toast
                    .makeText(
                        context,
                        context.getString(R.string.user_added),
                        Toast.LENGTH_SHORT,
                    ).show()
            } catch (e: Exception) {
                Log.e("HouseholdService", "Error when adding the user", e)
                Toast
                    .makeText(
                        context,
                        context.getString(R.string.user_not_found),
                        Toast.LENGTH_SHORT,
                    ).show()
            }
        }

        override suspend fun joinHouseholdById(
            householdId: String,
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
                    Toast
                        .makeText(
                            context,
                            context.getString(R.string.household_not_found),
                            Toast.LENGTH_SHORT,
                        ).show()
                    return
                }

                val batch = firestore.batch()

                userId?.let { uid ->
                    val userRef = firestore.collection("users").document(uid)
                    batch.update(userRef, "householdId", householdId)
                }

                val householdRef = firestore.collection("households").document(householdId)
                batch.update(householdRef, "users", FieldValue.arrayUnion(userId))

                userId?.let { uid ->
                    val itemsSnapshot =
                        firestore
                            .collection("foodItems")
                            .whereEqualTo("userId", uid)
                            .get()
                            .await()

                    for (doc in itemsSnapshot.documents) {
                        batch.update(doc.reference, "householdId", householdId)
                    }
                }

                batch.commit().await()

                logUserActivity(user, householdId, EventType.USER_JOINED)

                householdSnapshot
                    .toObject(Household::class.java)
                    ?.let(onSuccess)
            } catch (e: Exception) {
                Log.e("HouseholdService", "Error joining household with ID: $householdId", e)
                Toast
                    .makeText(
                        context,
                        context.getString(R.string.household_not_found),
                        Toast.LENGTH_SHORT,
                    ).show()
            }
        }

        override suspend fun logUserActivity(
            user: User,
            householdId: String,
            type: EventType,
        ) {
            val activity =
                Activity(
                    userId = user.id,
                    householdId = householdId,
                    type = type,
                    userName = user.displayName ?: "",
                    timestamp = System.currentTimeMillis(),
                )
            firestore
                .collection("activities")
                .add(activity)
                .await()
                .also { ref ->
                    ref.update("id", ref.id)
                }
        }

        override suspend fun updateHouseholdType(
            ownerId: String,
            newType: HouseholdType,
            selectedUser: String?,
            users: List<String>,
        ): List<String> =
            try {
                val batch = firestore.batch()
                var updatedUsers = users
                val householdId = getHouseholdId()

                when (newType) {
                    HouseholdType.SINGLE -> {
                        val usersToRemove = users.filter { it != ownerId && it != selectedUser }
                        val householdIdNonNull =
                            householdId
                                ?: throw IllegalStateException("Household ID is null")
                        batch.update(
                            firestore.collection("households").document(householdIdNonNull),
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

                    HouseholdType.PAIR ->
                        if (selectedUser != null) {
                            val usersToRemove = users.filter { it != ownerId && it != selectedUser }
                            val householdIdNonNull =
                                householdId
                                    ?: throw IllegalStateException("Household ID is null")

                            batch.update(
                                firestore.collection("households").document(householdIdNonNull),
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

                    HouseholdType.FAMILY -> {}
                    HouseholdType.SHARED_APARTMENT -> {}
                }

                householdId?.let {
                    firestore
                        .collection("households")
                        .document(it)
                        .update("type", newType)
                        .await()
                }

                updatedUsers
            } catch (e: Exception) {
                Log.e("HouseholdService", "Error updating household type", e)
                users
            }
    }

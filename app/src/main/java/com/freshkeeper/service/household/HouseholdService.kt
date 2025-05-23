package com.freshkeeper.service.household

import com.freshkeeper.model.Activity
import com.freshkeeper.model.EventType
import com.freshkeeper.model.FoodItem
import com.freshkeeper.model.Household
import com.freshkeeper.model.Member
import com.freshkeeper.model.ProfilePicture
import com.freshkeeper.model.Statistics
import com.freshkeeper.model.User
import kotlinx.coroutines.CoroutineScope

interface HouseholdService {
    suspend fun getHousehold(onResult: (Household) -> Unit)

    suspend fun getHouseholdId(): String?

    suspend fun updateHouseholdName(newName: String)

    suspend fun getMembers(
        onResult: (List<Member>?) -> Unit,
        onFailure: (Exception) -> Unit,
    )

    suspend fun getUserDetails(
        userIds: List<String>,
        coroutineScope: CoroutineScope,
        onResult: (List<Member>?) -> Unit,
        onFailure: (Exception) -> Unit,
    )

    suspend fun getActivities(
        onResult: (List<Activity>?) -> Unit,
        onFailure: (Exception) -> Unit,
    )

    suspend fun getExpiredProducts(): List<FoodItem>

    suspend fun getAllFoodItems(): List<FoodItem>

    suspend fun calculateStatistics(
        expired: List<FoodItem>,
        allItems: List<FoodItem>,
    ): Statistics

    suspend fun removeActivity(
        activity: Activity,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit,
    )

    suspend fun getProfilePicture(userId: String): ProfilePicture?

    suspend fun getFoodItems(): List<FoodItem>

    suspend fun createHousehold(
        name: String,
        type: String,
        onSuccess: (Household) -> Unit,
    )

    suspend fun leaveHousehold()

    suspend fun deleteHousehold(onSuccess: () -> Unit)

    suspend fun deleteProducts()

    suspend fun addProducts()

    suspend fun addUserById(
        userId: String,
        onSuccess: (User) -> Unit,
    )

    suspend fun joinHouseholdById(
        householdId: String,
        onSuccess: (Household) -> Unit,
    )

    suspend fun logUserActivity(
        user: User,
        householdId: String,
        type: EventType,
    )

    suspend fun updateHouseholdType(
        ownerId: String,
        newType: String,
        selectedUser: String?,
        users: List<String>,
    ): List<String>
}

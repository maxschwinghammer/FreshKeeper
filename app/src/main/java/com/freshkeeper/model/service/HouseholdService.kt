package com.freshkeeper.model.service

import com.freshkeeper.model.Activity
import com.freshkeeper.model.FoodItem
import com.freshkeeper.model.Member
import com.freshkeeper.model.ProfilePicture
import com.freshkeeper.model.User
import kotlinx.coroutines.CoroutineScope

interface HouseholdService {
    suspend fun getHouseholdId(
        onResult: (String?) -> Unit,
        onFailure: () -> Unit,
    )

    suspend fun getMembers(
        householdId: String,
        coroutineScope: CoroutineScope,
        onResult: (List<Member>?) -> Unit,
        onFailure: (Exception) -> Unit,
    )

    suspend fun loadUserDetails(
        userIds: List<User?>,
        coroutineScope: CoroutineScope,
        onResult: (List<Member>?) -> Unit,
        onFailure: (Exception) -> Unit,
    )

    suspend fun getActivities(
        householdId: String?,
        onResult: (List<Activity>?) -> Unit,
        onFailure: (Exception) -> Unit,
    )

    suspend fun getFoodWasteData(
        householdId: String?,
        onResult: (List<FoodItem>) -> Unit,
        onFailure: (Exception) -> Unit,
    )

    suspend fun removeActivity(
        activity: Activity,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit,
    )

    suspend fun getProfilePicture(profilePictureId: String): ProfilePicture?

    suspend fun getFoodItems(householdId: String?): List<FoodItem>
}

package com.freshkeeper.service

import com.freshkeeper.model.Activity
import com.freshkeeper.model.FoodItem
import com.freshkeeper.model.Household
import com.freshkeeper.model.Member
import com.freshkeeper.model.ProfilePicture
import kotlinx.coroutines.CoroutineScope

interface HouseholdService {
    suspend fun getHousehold(
        onResult: (Household?) -> Unit,
        onFailure: () -> Unit,
    )

    suspend fun getHouseholdId(
        onResult: (String?) -> Unit,
        onFailure: () -> Unit,
    )

    suspend fun getMembers(
        coroutineScope: CoroutineScope,
        onResult: (List<Member>?) -> Unit,
        onFailure: (Exception) -> Unit,
    )

    suspend fun loadUserDetails(
        userIds: List<String>,
        coroutineScope: CoroutineScope,
        onResult: (List<Member>?) -> Unit,
        onFailure: (Exception) -> Unit,
    )

    suspend fun getActivities(
        onResult: (List<Activity>?) -> Unit,
        onFailure: (Exception) -> Unit,
    )

    suspend fun getFoodWasteData(
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

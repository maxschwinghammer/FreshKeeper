package com.freshkeeper.screens.home.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.freshkeeper.model.FoodItem
import com.freshkeeper.screens.AppViewModel
import com.freshkeeper.service.household.HouseholdService
import com.freshkeeper.service.membership.MembershipService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val householdService: HouseholdService,
        private val membershipService: MembershipService,
    ) : AppViewModel() {
        private val _allFoodItems = MutableLiveData<List<FoodItem>>()
        val allFoodItems: LiveData<List<FoodItem>> = _allFoodItems

        private val _expiringSoonItems = MutableLiveData<List<FoodItem>>()
        val expiringSoonItems: LiveData<List<FoodItem>> = _expiringSoonItems

        private val _expiredItems = MutableLiveData<List<FoodItem>>()
        val expiredItems: LiveData<List<FoodItem>> = _expiredItems

        private val _householdId = MutableLiveData<String?>()
        val householdId: LiveData<String?> = _householdId

        private val _isMember = MutableLiveData<Boolean>()
        val isMember: LiveData<Boolean> = _isMember

        init {
            getHouseholdId()
            checkMembershipStatus()
        }

        private fun checkMembershipStatus() {
            launchCatching {
                val status = membershipService.isMember()
                _isMember.value = status
            }
        }

        private fun getHouseholdId() {
            launchCatching {
                try {
                    householdService.getHouseholdId(
                        onResult = { householdId ->
                            _householdId.value = householdId
                        },
                    )
                    getFoodItems()
                } catch (e: Exception) {
                    Log.e("HomeViewModel", "Error loading householdId from Firestore", e)
                }
            }
        }

        private suspend fun getFoodItems() {
            try {
                val foodItems = householdService.getFoodItems(householdId.value)
                _allFoodItems.value = foodItems
                _expiringSoonItems.value =
                    foodItems
                        .filter { it.daysDifference in 0..30 }
                        .sortedBy { it.expiryTimestamp }
                _expiredItems.value =
                    foodItems
                        .filter { it.daysDifference < 0 }
                        .sortedByDescending { it.expiryTimestamp }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error loading food items from Firestore", e)
            }
        }
    }

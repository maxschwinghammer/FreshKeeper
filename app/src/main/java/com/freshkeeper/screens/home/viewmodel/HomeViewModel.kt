package com.freshkeeper.screens.home.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.freshkeeper.model.FoodItem
import com.freshkeeper.service.HouseholdService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val householdService: HouseholdService,
    ) : ViewModel() {
        private val _allFoodItems = MutableLiveData<List<FoodItem>>()
        val allFoodItems: LiveData<List<FoodItem>> = _allFoodItems

        private val _expiringSoonItems = MutableLiveData<List<FoodItem>>()
        val expiringSoonItems: LiveData<List<FoodItem>> = _expiringSoonItems

        private val _expiredItems = MutableLiveData<List<FoodItem>>()
        val expiredItems: LiveData<List<FoodItem>> = _expiredItems

        private val _householdId = MutableLiveData<String?>()
        val householdId: LiveData<String?> = _householdId

        init {
            loadHouseholdId()
        }

        private fun loadHouseholdId() {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    householdService.getHouseholdId(
                        onResult = { householdId ->
                            _householdId.value = householdId
                        },
                        onFailure = {
                            Log.e(
                                "HouseholdViewModel",
                                "Error loading householdId",
                            )
                        },
                    )
                    loadFoodItemsFromService()
                } catch (e: Exception) {
                    Log.e("HomeViewModel", "Error loading householdId from Firestore", e)
                }
            }
        }

        private suspend fun loadFoodItemsFromService() {
            try {
                val foodItems = householdService.getFoodItems(householdId.value)
                withContext(Dispatchers.Main) {
                    _allFoodItems.value = foodItems
                    _expiringSoonItems.value =
                        foodItems
                            .filter { it.daysDifference in 0..30 }
                            .sortedBy { it.expiryTimestamp }
                    _expiredItems.value =
                        foodItems
                            .filter { it.daysDifference < 0 }
                            .sortedByDescending { it.expiryTimestamp }
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error loading food items from Firestore", e)
            }
        }
    }

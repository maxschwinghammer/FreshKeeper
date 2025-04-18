package com.freshkeeper.screens.household.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.freshkeeper.model.Activity
import com.freshkeeper.model.FoodItem
import com.freshkeeper.model.Household
import com.freshkeeper.model.Member
import com.freshkeeper.model.Nutriments
import com.freshkeeper.screens.AppViewModel
import com.freshkeeper.service.household.HouseholdService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HouseholdViewModel
    @Inject
    constructor(
        private val householdService: HouseholdService,
    ) : AppViewModel() {
        private val _members = MutableLiveData<List<Member>?>()
        val members: LiveData<List<Member>?> = _members

        private val _activities = MutableLiveData<List<Activity>?>()
        val activities: LiveData<List<Activity>?> = _activities

        private val _storyActivities = MutableLiveData<List<Activity>?>()
        val storyActivities: LiveData<List<Activity>?> = _storyActivities

        private val _totalWaste = MutableLiveData<Int>()
        val totalWaste: LiveData<Int> = _totalWaste

        private val _averageWaste = MutableLiveData<Float>()
        val averageWaste: LiveData<Float> = _averageWaste

        private val _daysWithoutWaste = MutableLiveData<Int>()
        val daysWithoutWaste: LiveData<Int> = _daysWithoutWaste

        private val _mostWastedItems = MutableLiveData<List<FoodItem>>()
        val mostWastedItems: LiveData<List<FoodItem>> = _mostWastedItems

        private val _wasteReduction = MutableLiveData<Int>()
        val wasteReduction: LiveData<Int> = _wasteReduction

        private val _usedItemsPercentage = MutableLiveData<Int>()
        val usedItemsPercentage: LiveData<Int> = _usedItemsPercentage

        private val _mostWastedCategory = MutableLiveData<String>()
        val mostWastedCategory: LiveData<String> = _mostWastedCategory

        private val _expiredDates = MutableLiveData<List<Long>>()
        val expiredDates: LiveData<List<Long>> = _expiredDates

        private val _averageNutriments = MutableLiveData<Nutriments>()
        val averageNutriments: LiveData<Nutriments> = _averageNutriments

        private val _averageNutriScore = MutableLiveData<String>()
        val averageNutriScore: LiveData<String> = _averageNutriScore

        private val _isInHousehold = MutableLiveData(false)
        val isInHousehold: LiveData<Boolean> = _isInHousehold

        private val _household = MutableLiveData<Household?>()
        val household: MutableLiveData<Household?> = _household

        init {
            getHousehold()
            _storyActivities.value =
                listOf(
                    Activity(
                        id = "1",
                        userId = "1",
                        type = "user_joined",
                        userName = "Tim",
                        timestamp = System.currentTimeMillis(),
                    ),
                    Activity(
                        id = "2",
                        userId = "2",
                        type = "add_product",
                        userName = "Emma",
                        productName = "Eier",
                        timestamp = System.currentTimeMillis(),
                    ),
                    Activity(
                        id = "3",
                        userId = "3",
                        type = "consumed",
                        userName = "Paul",
                        productName = "Milch",
                        timestamp = System.currentTimeMillis(),
                    ),
                )
        }

        private fun getHousehold() {
            launchCatching {
                householdService.getHousehold(
                    onResult = { household ->
                        _household.value = household
                        _isInHousehold.value = true
                        getHouseholdData()
                    },
                )
            }
        }

        private fun getHouseholdData() {
            launchCatching {
                householdService.getMembers(
                    onResult = { _members.value = it },
                    onFailure = { Log.e("HouseholdViewModel", "Error loading members") },
                )
                householdService.getActivities(
                    onResult = { _activities.value = it },
                    onFailure = { Log.e("HouseholdViewModel", "Error loading activities") },
                )
                householdService.getFoodWasteData(
                    onResult = { _totalWaste.value = it.size },
                    onFailure = {
                        Log.e(
                            "HouseholdViewModel",
                            "Error loading food waste data",
                        )
                    },
                )
            }
        }

        suspend fun removeActivity(activity: Activity) {
            householdService.removeActivity(
                activity,
                onSuccess = { _activities.value = _activities.value?.filter { it != activity } },
                onFailure = { Log.e("HouseholdViewModel", "Error removing activity") },
            )
        }
    }

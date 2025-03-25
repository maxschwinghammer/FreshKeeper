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
            _expiredDates.value =
                listOf(
                    System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000,
                    System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000,
                    System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000,
                    System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000,
                    System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000,
                    System.currentTimeMillis() - 6 * 24 * 60 * 60 * 1000,
                    System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000,
                    System.currentTimeMillis() - 10 * 24 * 60 * 60 * 1000,
                    System.currentTimeMillis() - 10 * 24 * 60 * 60 * 1000,
                    System.currentTimeMillis() - 10 * 24 * 60 * 60 * 1000,
                    System.currentTimeMillis() - 17 * 24 * 60 * 60 * 1000,
                    System.currentTimeMillis() - 17 * 24 * 60 * 60 * 1000,
                    System.currentTimeMillis() - 19 * 24 * 60 * 60 * 1000,
                    System.currentTimeMillis() - 20 * 24 * 60 * 60 * 1000,
                    System.currentTimeMillis() - 21 * 24 * 60 * 60 * 1000,
                )
            _averageNutriments.value =
                Nutriments(
                    energyKcal = 250.0,
                    fat = 10.5,
                    carbohydrates = 30.0,
                    sugars = 15.0,
                    fiber = 4.5,
                    proteins = 8.0,
                    salt = 0.5,
                )
            _averageNutriScore.value = "C"
        }

        private fun getHousehold() {
            launchCatching {
                householdService.getHousehold(
                    onResult = { household ->
                        _household.value = household
                        _isInHousehold.value = true

                        getHouseholdData()
                    },
                    onFailure = {
                        _household.value = Household()
                        _isInHousehold.value = false
                        _members.value = emptyList()
                        _activities.value = emptyList()
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

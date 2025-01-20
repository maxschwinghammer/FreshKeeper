package com.freshkeeper.screens.household.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freshkeeper.model.Activity
import com.freshkeeper.model.Member
import com.freshkeeper.model.service.HouseholdService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HouseholdViewModel
    @Inject
    constructor(
        private val householdService: HouseholdService,
    ) : ViewModel() {
        private val _members = MutableLiveData<List<Member>?>()
        val members: LiveData<List<Member>?> = _members

        private val _activities = MutableLiveData<List<Activity>?>()
        val activities: LiveData<List<Activity>?> = _activities

        private val _totalFoodWaste = MutableLiveData<Int>()
        val totalFoodWaste: LiveData<Int> = _totalFoodWaste

        private val _averageFoodWastePerDay = MutableLiveData<Float>()
        val averageFoodWastePerDay: LiveData<Float> = _averageFoodWastePerDay

        private val _daysWithNoWaste = MutableLiveData<Int>()
        val daysWithNoWaste: LiveData<Int> = _daysWithNoWaste

        private val _mostWastedItems = MutableLiveData<List<Pair<String, String>>>()
        val mostWastedItems: LiveData<List<Pair<String, String>>> = _mostWastedItems

        private val _isInHousehold = MutableLiveData(false)
        val isInHousehold: LiveData<Boolean> = _isInHousehold

        private val _householdId = MutableLiveData<String?>()
        val householdId: LiveData<String?> = _householdId

        init {
            loadHouseholdId()
        }

        private fun loadHouseholdId() {
            viewModelScope.launch {
                householdService.getHouseholdId(
                    onResult = { householdId ->
                        _householdId.value = householdId
                        householdId?.let { loadHouseholdData(it) }
                    },
                    onFailure = { Log.e("HouseholdViewModel", "Error loading householdId") },
                )
            }
        }

        private fun loadHouseholdData(householdId: String) {
            viewModelScope.launch {
                householdService.getMembers(
                    householdId,
                    coroutineScope = this,
                    onResult = { _members.value = it },
                    onFailure = { Log.e("HouseholdViewModel", "Error loading members") },
                )

                householdService.getActivities(
                    householdId,
                    onResult = { _activities.value = it },
                    onFailure = { Log.e("HouseholdViewModel", "Error loading activities") },
                )

                householdService.getFoodWasteData(
                    householdId,
                    onResult = { _totalFoodWaste.value = it.size },
                    onFailure = { Log.e("HouseholdViewModel", "Error loading food waste data") },
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

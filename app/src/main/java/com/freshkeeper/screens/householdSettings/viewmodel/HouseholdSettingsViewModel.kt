package com.freshkeeper.screens.householdSettings.viewmodel

import android.content.Context
import com.freshkeeper.model.Household
import com.freshkeeper.model.User
import com.freshkeeper.screens.AppViewModel
import com.freshkeeper.service.account.AccountService
import com.freshkeeper.service.household.HouseholdService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HouseholdSettingsViewModel
    @Inject
    constructor(
        private val accountService: AccountService,
        private val householdService: HouseholdService,
    ) : AppViewModel() {
        private val _user = MutableStateFlow(User())
        val user: StateFlow<User> = _user.asStateFlow()

        private val _household = MutableStateFlow(Household())
        val household: StateFlow<Household> = _household.asStateFlow()

        init {
            launchCatching {
                _user.value = accountService.getUserObject()
                getHousehold()
            }
        }

        fun addProducts() {
            launchCatching {
                householdService.addProducts()
            }
        }

        fun addUserById(
            userId: String,
            context: Context,
        ) {
            launchCatching {
                householdService.addUserById(
                    userId,
                    context,
                    onSuccess = { user ->
                        _household.value =
                            _household.value.copy(
                                users = _household.value.users + user.id,
                            )
                    },
                )
            }
        }

        fun createHousehold(
            name: String,
            type: String,
        ) {
            launchCatching {
                householdService.createHousehold(
                    name,
                    type,
                    onSuccess = { household ->
                        _household.value = household
                    },
                )
            }
        }

        fun deleteHousehold() {
            launchCatching {
                householdService.deleteHousehold(
                    onSuccess = { _household.value = Household() },
                )
            }
        }

        fun deleteProducts() {
            launchCatching {
                householdService.deleteProducts()
            }
        }

        private fun getHousehold() {
            launchCatching {
                householdService.getHousehold(
                    onResult = { household ->
                        _household.value = household
                    },
                )
            }
        }

        fun joinHouseholdById(
            householdId: String,
            context: Context,
        ) {
            launchCatching {
                householdService.joinHouseholdById(
                    householdId,
                    context,
                    onSuccess = { joinedHousehold ->
                        _household.value = joinedHousehold
                    },
                )
            }
        }

        fun leaveHousehold() {
            launchCatching {
                householdService.leaveHousehold()
            }
        }

        fun updateHouseholdName(newName: String) {
            launchCatching {
                householdService.updateHouseholdName(newName)
                _household.value = _household.value.copy(name = newName)
            }
        }

        fun updateHouseholdType(
            newType: String,
            selectedUser: String?,
        ) {
            val ownerId = _household.value.ownerId
            val users = _household.value.users

            launchCatching {
                val updatedUsers =
                    householdService.updateHouseholdType(
                        ownerId,
                        newType,
                        selectedUser,
                        users,
                    )
                _household.value = _household.value.copy(type = newType, users = updatedUsers)
            }
        }
    }

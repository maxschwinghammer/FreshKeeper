package com.freshkeeper.screens.profile.viewmodel

import com.freshkeeper.model.ProfilePicture
import com.freshkeeper.model.User
import com.freshkeeper.screens.AppViewModel
import com.freshkeeper.service.account.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel
    @Inject
    constructor(
        private val accountService: AccountService,
    ) : AppViewModel() {
        private val _user = MutableStateFlow<User?>(null)
        val user: StateFlow<User?> = _user.asStateFlow()

        private val _memberSinceDays = MutableStateFlow<Long>(0)
        val memberSinceDays: StateFlow<Long> = _memberSinceDays.asStateFlow()

        private val _profilePicture = MutableStateFlow<ProfilePicture?>(null)
        val profilePicture: StateFlow<ProfilePicture?> = _profilePicture.asStateFlow()

        fun getUserProfile(userId: String) {
            launchCatching {
                _user.value = accountService.getUserProfile(userId)
                _memberSinceDays.value =
                    accountService
                        .calculateDaysSince(user.value?.createdAt ?: 0)
                _profilePicture.value = accountService.getProfilePicture(user.value?.id ?: "")
            }
        }

        fun updateDisplayName(newDisplayName: String) {
            launchCatching {
                accountService.updateDisplayName(newDisplayName)
            }
        }
    }

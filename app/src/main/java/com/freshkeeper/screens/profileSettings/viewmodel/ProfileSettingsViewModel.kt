package com.freshkeeper.screens.profileSettings.viewmodel

import androidx.lifecycle.viewModelScope
import com.freshkeeper.model.ProfilePicture
import com.freshkeeper.model.User
import com.freshkeeper.model.service.AccountService
import com.freshkeeper.screens.AppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileSettingsViewModel
    @Inject
    constructor(
        private val accountService: AccountService,
    ) : AppViewModel() {
        private val _user = MutableStateFlow(User())
        val user: StateFlow<User> = _user.asStateFlow()

        private val _profilePicture = MutableStateFlow<ProfilePicture?>(null)
        val profilePicture: StateFlow<ProfilePicture?> = _profilePicture.asStateFlow()

        init {
            launchCatching {
                _user.value = accountService.getUserObject()
            }
        }

        fun onUpdateDisplayNameClick(newDisplayName: String) {
            launchCatching {
                accountService.updateDisplayName(newDisplayName)
                _user.value = accountService.getUserObject()
            }
        }

        fun onSignOutClick(navigateToSplash: () -> Unit) {
            launchCatching {
                accountService.signOut()
                navigateToSplash()
            }
        }

        fun onResetPasswordClick(navigateToSplash: () -> Unit) {
            launchCatching {
                accountService.resetPassword()
                navigateToSplash()
            }
        }

        fun onChangeEmailClick(newEmail: String) {
            launchCatching {
                accountService.changeEmail(newEmail)
            }
        }

        fun onDeleteAccountClick() {
            launchCatching {
                accountService.deleteAccount()
            }
        }

        fun updateProfilePicture(profilePicture: String) {
            launchCatching {
                accountService.updateProfilePicture(profilePicture)
                _user.value = accountService.getUserObject()
            }
        }

        fun getProfilePicture() {
            viewModelScope.launch {
                try {
                    val profilePicture = accountService.getProfilePicture(user.value.id)
                    _profilePicture.value = profilePicture
                } catch (e: Exception) {
                    _profilePicture.value = null
                }
            }
        }
    }

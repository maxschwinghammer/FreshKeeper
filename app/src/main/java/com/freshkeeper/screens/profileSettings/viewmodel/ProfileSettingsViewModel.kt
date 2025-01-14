package com.freshkeeper.screens.profileSettings.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
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

        private val _profilePicture = MutableLiveData<String?>()
        val profilePicture: LiveData<String?> get() = _profilePicture

        init {
            launchCatching {
                _user.value = accountService.getUserProfile()
            }
        }

        fun onUpdateDisplayNameClick(newDisplayName: String) {
            launchCatching {
                accountService.updateDisplayName(newDisplayName)
                _user.value = accountService.getUserProfile()
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

        fun updateProfilePicture(base64: String) {
            launchCatching {
                accountService.updateProfilePicture(base64)
                _user.value = accountService.getUserProfile()
            }
        }

        fun getProfilePicture() {
            viewModelScope.launch {
                try {
                    val base64Image = accountService.getProfilePicture()
                    _profilePicture.value = base64Image
                } catch (e: Exception) {
                    _profilePicture.value = null
                }
            }
        }
    }

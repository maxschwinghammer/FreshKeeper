package com.freshkeeper.screens.profileSettings.viewmodel

import android.content.Context
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
                getProfilePicture()
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
                navigateToSplash()
                accountService.signOut()
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
                _profilePicture.value = ProfilePicture(profilePicture, "base64")
                _user.value = accountService.getUserObject()
                accountService.updateProfilePicture(profilePicture)
            }
        }

        private fun getProfilePicture() {
            launchCatching {
                try {
                    val profilePicture = accountService.getProfilePicture(user.value.id)
                    _profilePicture.value = profilePicture
                } catch (e: Exception) {
                    _profilePicture.value = null
                    e.printStackTrace()
                }
            }
        }

        fun downloadUserData(
            userId: String,
            context: Context,
        ) {
            launchCatching {
                accountService.downloadUserData(userId, context)
            }
        }
    }

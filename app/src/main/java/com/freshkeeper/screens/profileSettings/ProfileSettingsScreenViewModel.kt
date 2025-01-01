package com.freshkeeper.screens.profileSettings

import com.freshkeeper.model.User
import com.freshkeeper.model.service.AccountService
import com.freshkeeper.screens.AppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ProfileSettingsScreenViewModel
    @Inject
    constructor(
        private val accountService: AccountService,
    ) : AppViewModel() {
        private val _user = MutableStateFlow(User())
        val user: StateFlow<User> = _user.asStateFlow()

        init {
            launchCatching {
                _user.value = accountService.getUserProfile()
            }
        }

        fun onBiometricSwitchClick() {
            _user.value = _user.value.copy(isBiometricEnabled = !_user.value.isBiometricEnabled)
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
    }

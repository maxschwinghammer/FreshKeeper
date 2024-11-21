package com.freshkeeper.screens.profile

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

        fun onUpdateDisplayNameClick(newDisplayName: String) {
            launchCatching {
                accountService.updateDisplayName(newDisplayName)
                _user.value = accountService.getUserProfile()
            }
        }

        fun onSignInClick() {
            // TODO: Add sign in logic
        }

        fun onSignOutClick(navigateToSplash: () -> Unit) {
            launchCatching {
                accountService.signOut()
                navigateToSplash()
            }
        }

        fun onDeleteAccountClick() {
            launchCatching {
                accountService.deleteAccount()
            }
        }
    }

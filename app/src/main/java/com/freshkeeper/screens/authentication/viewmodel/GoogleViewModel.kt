package com.freshkeeper.screens.authentication.viewmodel

import androidx.credentials.Credential
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.freshkeeper.R
import com.freshkeeper.screens.AppViewModel
import com.freshkeeper.service.googleAccount.GoogleAccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class GoogleViewModel
    @Inject
    constructor(
        private val googleAccountService: GoogleAccountService,
    ) : AppViewModel() {
        @Suppress("ktlint:standard:backing-property-naming")
        private val _errorMessage = MutableStateFlow<Int?>(null)

        fun onSignInWithGoogle(
            credential: Credential,
            navController: NavController,
            activity: FragmentActivity,
        ) {
            launchCatching {
                googleAccountService.signInWithGoogle(credential, activity, {
                    navController.navigate("home") { launchSingleTop = true }
                }, { _errorMessage.value = R.string.biometric_auth_failed })
            }
        }
    }

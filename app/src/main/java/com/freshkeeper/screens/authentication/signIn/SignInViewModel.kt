package com.freshkeeper.screens.authentication.signIn

import android.util.Log
import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import androidx.navigation.NavController
import com.freshkeeper.ERROR_TAG
import com.freshkeeper.UNEXPECTED_CREDENTIAL
import com.freshkeeper.model.service.AccountService
import com.freshkeeper.screens.AppViewModel
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SignInViewModel
    @Inject
    constructor(
        private val accountService: AccountService,
    ) : AppViewModel() {
        private val _email = MutableStateFlow("")
        val email: StateFlow<String> = _email.asStateFlow()

        private val _password = MutableStateFlow("")
        val password: StateFlow<String> = _password.asStateFlow()

        fun updateEmail(newEmail: String) {
            _email.value = newEmail
        }

        fun updatePassword(newPassword: String) {
            _password.value = newPassword
        }

        fun onSignInClick(navController: NavController) {
            launchCatching {
                accountService.signInWithEmail(_email.value, _password.value)
                navController.navigate("home") { launchSingleTop = true }
            }
        }

        fun onSignInWithGoogle(
            credential: Credential,
            navController: NavController,
        ) {
            launchCatching {
                if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    accountService.signInWithGoogle(googleIdTokenCredential.idToken)
                    navController.navigate("home") { launchSingleTop = true }
                } else {
                    Log.e(ERROR_TAG, UNEXPECTED_CREDENTIAL)
                }
            }
        }
    }

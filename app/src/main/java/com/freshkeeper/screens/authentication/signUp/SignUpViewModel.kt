package com.freshkeeper.screens.authentication.signUp

import android.util.Log
import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import androidx.navigation.NavController
import com.freshkeeper.ERROR_TAG
import com.freshkeeper.R
import com.freshkeeper.model.service.AccountService
import com.freshkeeper.screens.AppViewModel
import com.freshkeeper.screens.authentication.isValidEmail
import com.freshkeeper.screens.authentication.isValidPassword
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel
    @Inject
    constructor(
        private val accountService: AccountService,
    ) : AppViewModel() {
        private val _email = MutableStateFlow("")
        val email: StateFlow<String> = _email.asStateFlow()

        private val _password = MutableStateFlow("")
        val password: StateFlow<String> = _password.asStateFlow()

        private val _confirmPassword = MutableStateFlow("")
        val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

        private val _errorMessage = MutableStateFlow<Int?>(null)
        val errorMessage: StateFlow<Int?> = _errorMessage.asStateFlow()

        fun updateEmail(newEmail: String) {
            _email.value = newEmail
        }

        fun updatePassword(newPassword: String) {
            _password.value = newPassword
        }

        fun updateConfirmPassword(newConfirmPassword: String) {
            _confirmPassword.value = newConfirmPassword
        }

        fun onSignUpClick(navController: NavController) {
            launchCatching {
                try {
                    if (!_email.value.isValidEmail()) {
                        _errorMessage.value = R.string.email_invalid
                        return@launchCatching
                    }

                    if (!_password.value.isValidPassword()) {
                        _errorMessage.value = R.string.password_invalid
                        return@launchCatching
                    }

                    if (_password.value != _confirmPassword.value) {
                        _errorMessage.value = R.string.password_mismatch
                        return@launchCatching
                    }

                    try {
                        accountService.linkAccountWithEmail(_email.value, _password.value)
                        accountService.sendEmailVerification()
                        _errorMessage.value = null
                        navController.navigate("home") { launchSingleTop = true }
                    } catch (e: Exception) {
                        if (e is FirebaseAuthUserCollisionException) {
                            _errorMessage.value = R.string.email_already_exists
                        } else {
                            _errorMessage.value = R.string.sign_up_error
                        }
                    }
                } catch (e: Exception) {
                    Log.e("SignUp", "Error during sign-up: ${e.message}", e)
                    _errorMessage.value = R.string.sign_up_error
                }
            }
        }

        fun onSignUpWithGoogle(
            credential: Credential,
            navController: NavController,
        ) {
            launchCatching {
                if (credential is CustomCredential &&
                    credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleIdTokenCredential =
                        GoogleIdTokenCredential
                            .createFrom(credential.data)
                    Log.d("GoogleCredential", "ID Token: ${googleIdTokenCredential.idToken}")
                    accountService.linkAccountWithGoogle(googleIdTokenCredential.idToken)
                    navController.navigate("home") { launchSingleTop = true }
                } else {
                    Log.e(ERROR_TAG, "Unexpected credential type: ${credential.type}")
                }
            }
        }
    }

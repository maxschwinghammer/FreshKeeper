package com.freshkeeper.screens.authentication.signIn

import androidx.navigation.NavController
import com.freshkeeper.R
import com.freshkeeper.model.service.AccountService
import com.freshkeeper.screens.AppViewModel
import com.freshkeeper.screens.authentication.isValidEmail
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

        private val _emailSent = MutableStateFlow(false)
        val emailSent: StateFlow<Boolean> = _emailSent.asStateFlow()

        private val _password = MutableStateFlow("")
        val password: StateFlow<String> = _password.asStateFlow()

        private val _errorMessage = MutableStateFlow<Int?>(null)
        val errorMessage: StateFlow<Int?> = _errorMessage.asStateFlow()

        fun updateEmail(newEmail: String) {
            _email.value = newEmail
        }

        fun updatePassword(newPassword: String) {
            _password.value = newPassword
        }

        fun onSignInClick(navController: NavController) {
            launchCatching {
                if (_email.value.isEmpty()) {
                    _errorMessage.value = R.string.no_email
                    return@launchCatching
                }
                if (_password.value.isEmpty()) {
                    _errorMessage.value = R.string.no_password
                    return@launchCatching
                }
                if (!_email.value.isValidEmail()) {
                    _errorMessage.value = R.string.email_invalid
                    return@launchCatching
                }
                _errorMessage.value = null
                accountService.signInWithEmail(_email.value, _password.value)
                navController.navigate("home") { launchSingleTop = true }
            }
        }

        fun onForgotPasswordClick(
            email: String,
            navigateToSplash: () -> Unit,
        ) {
            launchCatching {
                accountService.forgotPassword(email)
                _emailSent.value = true
                navigateToSplash()
            }
        }

        /*fun retrieveSavedCredentials(
            context: Context,
            onCredentialsRetrieved: (email: String, password: String) -> Unit,
        ) {
            val credentialManager = CredentialManager.create(context)
            val request = GetCredentialRequest(listOf(GetPasswordOption()))

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Hier den richtigen Context nutzen
                    val response = credentialManager.getCredential(request) // Kein zusätzlicher Kontext nötig
                    val credential = response.credential

                    if (credential is PasswordCredential) {
                        withContext(Dispatchers.Main) {
                            onCredentialsRetrieved(credential.id, credential.password)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            onCredentialsRetrieved("", "") // Kein Passwort gefunden
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        onCredentialsRetrieved("", "") // Fehler beim Abrufen
                    }
                }
            }
        }*/
    }

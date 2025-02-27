package com.freshkeeper.screens.authentication.viewmodel

import android.content.Context
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.freshkeeper.R
import com.freshkeeper.screens.AppViewModel
import com.freshkeeper.screens.authentication.isValidEmail
import com.freshkeeper.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

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

        fun onSignInClick(
            navController: NavController,
            context: Context,
            activity: FragmentActivity,
        ) {
            if (isBiometricEnabled(context)) {
                launchCatching {
                    val authenticated = authenticateBiometric(context, activity)
                    if (authenticated) {
                        navController.navigate("home") { launchSingleTop = true }
                    } else {
                        _errorMessage.value = R.string.biometric_auth_failed
                    }
                }
            } else {
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

        private fun isBiometricEnabled(context: Context): Boolean {
            val sharedPreferences =
                context.getSharedPreferences(
                    "user_preferences",
                    Context.MODE_PRIVATE,
                )
            return sharedPreferences.getBoolean("biometric_enabled", false)
        }

        private suspend fun authenticateBiometric(
            context: Context,
            activity: FragmentActivity,
        ): Boolean =
            suspendCancellableCoroutine { continuation ->
                val executor = ContextCompat.getMainExecutor(context)
                val biometricPrompt =
                    BiometricPrompt(
                        activity,
                        executor,
                        object : BiometricPrompt.AuthenticationCallback() {
                            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                                continuation.resume(true)
                                launchCatching {
                                    accountService.signInWithEmail(_email.value, _password.value)
                                }
                            }

                            override fun onAuthenticationFailed() {
                                continuation.resume(false)
                            }

                            override fun onAuthenticationError(
                                errorCode: Int,
                                errString: CharSequence,
                            ) {
                                continuation.resume(false)
                            }
                        },
                    )

                val promptInfo =
                    BiometricPrompt.PromptInfo
                        .Builder()
                        .setTitle("Biometric authentication")
                        .setSubtitle("Please authenticate yourself to continue")
                        .setNegativeButtonText("Cancel")
                        .build()

                biometricPrompt.authenticate(promptInfo)
            }
    }

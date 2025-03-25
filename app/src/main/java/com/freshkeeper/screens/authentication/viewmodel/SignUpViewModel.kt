package com.freshkeeper.screens.authentication.viewmodel

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.freshkeeper.R
import com.freshkeeper.screens.AppViewModel
import com.freshkeeper.screens.authentication.isValidEmail
import com.freshkeeper.screens.authentication.isValidPassword
import com.freshkeeper.service.account.AccountService
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume

@HiltViewModel
class SignUpViewModel
    @Inject
    constructor(
        private val accountService: AccountService,
    ) : AppViewModel() {
        init {
            val languageCode = Locale.getDefault().language
            FirebaseAuth.getInstance().setLanguageCode(languageCode)
        }

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

        fun onSignUpClick(
            navController: NavController,
            context: Context,
            activity: FragmentActivity,
        ) {
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

                    accountService.signUpWithEmail(_email.value, _password.value)

                    try {
                        accountService.sendEmailVerification()
                    } catch (e: Exception) {
                        Log.e("SignUp", "Error sending email verification: ${e.message}", e)
                        _errorMessage.value = R.string.email_verification_failed
                        return@launchCatching
                    }

                    val user = Firebase.auth.currentUser
                    user?.let {
                        saveUserToFirestore(it.uid, _email.value)
                    }

                    _errorMessage.value = R.string.verify_email_prompt
                    checkEmailVerification(navController, context, activity)
                } catch (e: Exception) {
                    Log.e("SignUp", "Error during sign-up: ${e.message}", e)
                    _errorMessage.value = R.string.sign_up_error
                }
            }
        }

        private fun saveUserToFirestore(
            userId: String,
            email: String,
        ) {
            launchCatching {
                accountService.saveUserToFirestore(userId, email)
            }
        }

        private suspend fun checkEmailVerification(
            navController: NavController,
            context: Context,
            activity: FragmentActivity,
        ) {
            while (!accountService.getUserProfile().isEmailVerified) {
                try {
                    Firebase.auth.currentUser
                        ?.reload()
                        ?.await()
                } catch (e: Exception) {
                    Log.e("SignUp", "Error reloading user: ${e.message}", e)
                }
                kotlinx.coroutines.delay(3000)
            }
            navController.navigate("nameInput") { launchSingleTop = true }
            val enableBiometric = askForBiometricActivation(context)
            saveBiometricPreference(context, enableBiometric)
            if (enableBiometric) {
                val authenticated = authenticateBiometric(context, activity)
                if (!authenticated) {
                    Log.e("SignUp", "Biometric authentication failed")
                }
            }
        }

        private fun saveBiometricPreference(
            context: Context,
            enableBiometric: Boolean,
        ) {
            val sharedPreferences =
                context.getSharedPreferences(
                    "user_preferences",
                    Context.MODE_PRIVATE,
                )
            sharedPreferences.edit {
                putBoolean("biometric_enabled", enableBiometric)
            }
        }

        private suspend fun askForBiometricActivation(context: Context): Boolean =
            suspendCancellableCoroutine { continuation ->
                AlertDialog
                    .Builder(context)
                    .setTitle("Biometric authentication")
                    .setMessage("Would you like to activate biometric authentication?")
                    .setPositiveButton("Yes") { _, _ -> continuation.resume(true) }
                    .setNegativeButton("No") { _, _ -> continuation.resume(false) }
                    .setOnCancelListener { continuation.resume(false) }
                    .show()
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
                            override fun onAuthenticationSucceeded(
                                result: BiometricPrompt
                                    .AuthenticationResult,
                            ) {
                                continuation.resume(true)
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

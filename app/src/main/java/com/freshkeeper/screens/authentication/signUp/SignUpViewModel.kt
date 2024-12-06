package com.freshkeeper.screens.authentication.signUp

import android.util.Log
import androidx.navigation.NavController
import com.freshkeeper.R
import com.freshkeeper.model.service.AccountService
import com.freshkeeper.screens.AppViewModel
import com.freshkeeper.screens.authentication.isValidEmail
import com.freshkeeper.screens.authentication.isValidPassword
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import java.util.Locale
import javax.inject.Inject

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

                    accountService.signUpWithEmail(_email.value, _password.value)

                    try {
                        accountService.sendEmailVerification()
                    } catch (e: Exception) {
                        Log.e("SignUp", "Error sending email verification: ${e.message}", e)
                        _errorMessage.value = R.string.email_verification_failed
                        return@launchCatching
                    }

                    _errorMessage.value = R.string.verify_email_prompt
                    checkEmailVerification(navController)
                } catch (e: Exception) {
                    Log.e("SignUp", "Error during sign-up: ${e.message}", e)
                    _errorMessage.value = R.string.sign_up_error
                }
            }
        }

        private suspend fun checkEmailVerification(navController: NavController) {
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
            Log.d("SignUp", "Email verified, navigating to home")
            navController.navigate("home") { launchSingleTop = true }
        }
    }

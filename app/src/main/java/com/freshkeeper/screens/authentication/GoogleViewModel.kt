package com.freshkeeper.screens.authentication

import android.content.Context
import android.util.Log
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.freshkeeper.ERROR_TAG
import com.freshkeeper.R
import com.freshkeeper.UNEXPECTED_CREDENTIAL
import com.freshkeeper.model.service.AccountService
import com.freshkeeper.screens.AppViewModel
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

@HiltViewModel
class GoogleViewModel
    @Inject
    constructor(
        private val accountService: AccountService,
    ) : AppViewModel() {
        private val _errorMessage = MutableStateFlow<Int?>(null)

        fun onSignInWithGoogle(
            credential: Credential,
            navController: NavController,
            context: Context,
            activity: FragmentActivity,
        ) {
            if (isBiometricEnabled(context)) {
                launchCatching {
                    val authenticated = authenticateBiometric(context, activity, credential)
                    if (authenticated) {
                        navController.navigate("home") { launchSingleTop = true }
                    } else {
                        _errorMessage.value = R.string.biometric_auth_failed
                    }
                }
            } else {
                launchCatching {
                    if (credential is CustomCredential &&
                        credential.type ==
                        TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                    ) {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(
                                credential.data,
                            )
                        accountService.signInWithGoogle(googleIdTokenCredential.idToken)
                        navController.navigate("home") { launchSingleTop = true }
                    } else {
                        Log.e(ERROR_TAG, UNEXPECTED_CREDENTIAL)
                    }
                }
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
            credential: Credential?,
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
                                if (credential is CustomCredential &&
                                    credential.type
                                    == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                                ) {
                                    val googleIdTokenCredential =
                                        GoogleIdTokenCredential.createFrom(credential.data)
                                    launchCatching {
                                        accountService.signInWithGoogle(
                                            googleIdTokenCredential
                                                .idToken,
                                        )
                                        continuation.resume(true)
                                    }
                                } else {
                                    continuation.resume(false)
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
                        .setTitle("Biometrische Authentifizierung")
                        .setSubtitle("Bitte authentifizieren Sie sich, um fortzufahren")
                        .setNegativeButtonText("Abbrechen")
                        .build()

                biometricPrompt.authenticate(promptInfo)
            }
    }

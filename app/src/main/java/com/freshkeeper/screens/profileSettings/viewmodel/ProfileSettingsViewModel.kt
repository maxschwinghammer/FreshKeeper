package com.freshkeeper.screens.profileSettings.viewmodel

import android.content.Context
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.freshkeeper.R
import com.freshkeeper.model.ProfilePicture
import com.freshkeeper.model.User
import com.freshkeeper.screens.AppViewModel
import com.freshkeeper.service.account.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ProfileSettingsViewModel
    @Inject
    constructor(
        private val accountService: AccountService,
        @ApplicationContext private val context: Context,
    ) : AppViewModel() {
        private val _user = MutableStateFlow(User())
        val user: StateFlow<User> = _user.asStateFlow()

        private val _profilePicture = MutableStateFlow<ProfilePicture?>(null)
        val profilePicture: StateFlow<ProfilePicture?> = _profilePicture.asStateFlow()

        private val _isBiometricEnabled = MutableStateFlow(false)
        val isBiometricEnabled: StateFlow<Boolean> = _isBiometricEnabled.asStateFlow()

        init {
            launchCatching {
                _user.value = accountService.getUserObject()
                _isBiometricEnabled.value = accountService.getBiometricEnabled()
                getProfilePicture()
            }
        }

        fun onUpdateDisplayNameClick(newDisplayName: String) {
            launchCatching {
                accountService.updateDisplayName(newDisplayName)
                _user.value = accountService.getUserObject()
            }
        }

        fun onSignOutClick(navigateToSplash: () -> Unit) {
            launchCatching {
                navigateToSplash()
                accountService.signOut()
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

        fun updateProfilePicture(profilePicture: String) {
            launchCatching {
                _profilePicture.value = ProfilePicture(profilePicture, "base64")
                _user.value = accountService.getUserObject()
                accountService.updateProfilePicture(profilePicture)
            }
        }

        private fun getProfilePicture() {
            launchCatching {
                try {
                    val profilePicture = accountService.getProfilePicture(user.value.id)
                    _profilePicture.value = profilePicture
                } catch (e: Exception) {
                    _profilePicture.value = null
                    e.printStackTrace()
                }
            }
        }

        fun downloadUserData(userId: String) {
            launchCatching {
                accountService.downloadUserData(userId)
            }
        }

        fun onBiometricSwitchChanged(
            isChecked: Boolean,
            activity: FragmentActivity?,
        ) {
            if (!isChecked) {
                launchCatching {
                    accountService.updateBiometricEnabled(false)
                    _isBiometricEnabled.value = false
                }
            } else {
                activity?.let {
                    val executor = ContextCompat.getMainExecutor(context)
                    val biometricPrompt =
                        BiometricPrompt(
                            it,
                            executor,
                            object : BiometricPrompt.AuthenticationCallback() {
                                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                                    launchCatching {
                                        accountService.updateBiometricEnabled(true)
                                        _isBiometricEnabled.value = true
                                    }
                                }

                                override fun onAuthenticationFailed() {
                                    _isBiometricEnabled.value = false
                                }

                                override fun onAuthenticationError(
                                    errorCode: Int,
                                    errString: CharSequence,
                                ) {
                                    _isBiometricEnabled.value = false
                                }
                            },
                        )

                    val promptInfo =
                        BiometricPrompt.PromptInfo
                            .Builder()
                            .setTitle(context.getString(R.string.biometric_auth_title))
                            .setSubtitle(context.getString(R.string.biometric_auth_subtitle))
                            .setNegativeButtonText(context.getString(R.string.cancel))
                            .build()

                    biometricPrompt.authenticate(promptInfo)
                }
            }
        }
    }

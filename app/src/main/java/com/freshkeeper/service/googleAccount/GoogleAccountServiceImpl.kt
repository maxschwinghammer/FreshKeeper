package com.freshkeeper.service.googleAccount

import android.content.Context
import android.util.Log
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import androidx.fragment.app.FragmentActivity
import com.freshkeeper.ERROR_TAG
import com.freshkeeper.R
import com.freshkeeper.UNEXPECTED_CREDENTIAL
import com.freshkeeper.model.Membership
import com.freshkeeper.model.NotificationSettings
import com.freshkeeper.model.User
import com.freshkeeper.service.account.AccountService
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion
    .TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import java.time.LocalTime
import javax.inject.Inject
import kotlin.coroutines.resume

class GoogleAccountServiceImpl
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val accountService: AccountService,
    ) : GoogleAccountService {
        private val firestore = FirebaseFirestore.getInstance()

        override suspend fun authenticateBiometric(
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
                                if (credential is
                                        CustomCredential &&
                                    credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                                ) {
                                    val googleIdTokenCredential =
                                        GoogleIdTokenCredential
                                            .createFrom(credential.data)
                                    continuation.resume(
                                        runBlocking {
                                            accountService.signInWithGoogle(
                                                googleIdTokenCredential.idToken,
                                            )
                                            true
                                        },
                                    )
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
                        .setTitle(context.getString(R.string.biometric_auth_title))
                        .setSubtitle(context.getString(R.string.biometric_auth_subtitle))
                        .setNegativeButtonText(context.getString(R.string.cancel))
                        .build()
                biometricPrompt.authenticate(promptInfo)
            }

        override fun saveUserToFirestore(
            userId: String,
            email: String,
            displayName: String,
            profilePictureUrl: String?,
        ) {
            val usersRef = firestore.collection("users").document(userId)
            val membershipRef = firestore.collection("memberships").document(userId)
            val notificationRef = firestore.collection("notificationSettings").document(userId)

            usersRef.get().addOnSuccessListener { userSnapshot ->
                if (!userSnapshot.exists()) {
                    val user =
                        User(
                            id = userId,
                            email = email,
                            displayName = displayName,
                            createdAt = System.currentTimeMillis(),
                            provider = "google",
                        )
                    usersRef.set(user)

                    membershipRef.get().addOnSuccessListener { membershipSnapshot ->
                        if (!membershipSnapshot.exists()) {
                            membershipRef.set(Membership())
                        }
                    }

                    notificationRef.get().addOnSuccessListener { notifSnapshot ->
                        if (!notifSnapshot.exists()) {
                            val notificationSettings =
                                NotificationSettings(
                                    dailyNotificationTime = LocalTime.of(12, 0).toString(),
                                    timeBeforeExpiration = 2,
                                    dailyReminders = false,
                                    foodAdded = false,
                                    householdChanges = false,
                                    foodExpiring = false,
                                    tips = false,
                                    statistics = false,
                                )
                            notificationRef.set(notificationSettings)
                        }
                    }
                }
            }
        }

        override fun saveUserDocument(
            userId: String,
            email: String,
            displayName: String,
        ) {
            firestore
                .collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (!documentSnapshot.exists()) {
                        val user =
                            User(
                                id = userId,
                                email = email,
                                displayName = displayName,
                                createdAt = System.currentTimeMillis(),
                                provider = "google",
                            )
                        firestore
                            .collection("users")
                            .document(userId)
                            .set(user)
                            .addOnFailureListener { }
                    }
                }.addOnFailureListener { }
        }

        override suspend fun signInWithGoogle(
            credential: Credential,
            activity: FragmentActivity,
            onSuccess: () -> Unit,
            onError: (Int) -> Unit,
        ) {
            val isBiometricEnabled = runBlocking { accountService.getBiometricEnabled() }

            if (isBiometricEnabled) {
                if (authenticateBiometric(activity, credential)) {
                    onSuccess()
                } else {
                    onError(R.string.biometric_auth_failed)
                }
            } else {
                if (credential is CustomCredential &&
                    credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleIdTokenCredential =
                        GoogleIdTokenCredential
                            .createFrom(credential.data)
                    accountService.signInWithGoogle(googleIdTokenCredential.idToken)
                    val user = Firebase.auth.currentUser
                    user?.let {
                        val email = it.email.orEmpty()
                        val displayName = it.displayName.orEmpty()
                        val profilePictureUrl = it.photoUrl?.toString() ?: ""
                        if (profilePictureUrl.isNotEmpty()) {
                            saveUserToFirestore(it.uid, email, displayName, profilePictureUrl)
                        } else {
                            saveUserToFirestore(it.uid, email, displayName, null)
                        }
                    }
                    onSuccess()
                } else {
                    Log.e(ERROR_TAG, UNEXPECTED_CREDENTIAL)
                }
            }
        }
    }

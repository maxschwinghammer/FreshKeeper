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
import com.freshkeeper.model.ProfilePicture
import com.freshkeeper.model.User
import com.freshkeeper.service.account.AccountService
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion
    .TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import java.time.LocalTime
import javax.inject.Inject
import kotlin.coroutines.resume

class GoogleAccountServiceImpl
    @Inject
    constructor(
        private val accountService: AccountService,
    ) : GoogleAccountService {
        private val firestore = FirebaseFirestore.getInstance()

        init {
            generateSecretKey()
        }

        private fun generateSecretKey() {
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                "MySecretKey",
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setUserAuthenticationRequired(true)
                .setInvalidatedByBiometricEnrollment(true)
                .build()
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore"
            )
            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()
        }

        private fun getSecretKey(): SecretKey {
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            return keyStore.getKey("MySecretKey", null) as SecretKey
        }

        private fun getCipher(): Cipher {
            return Cipher.getInstance(
                "${KeyProperties.KEY_ALGORITHM_AES}/" +
                        "${KeyProperties.BLOCK_MODE_CBC}/" +
                        "${KeyProperties.ENCRYPTION_PADDING_PKCS7}"
            )
        }

        override suspend fun authenticateBiometric(
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
                                val cipher = result.cryptoObject?.cipher
                                if (cipher != null && credential is
                                        CustomCredential &&
                                    credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                                ) {
                                    val googleIdTokenCredential =
                                        GoogleIdTokenCredential
                                            .createFrom(credential.data)
                                    val decryptedData = cipher.doFinal(
                                        googleIdTokenCredential.idToken.toByteArray()
                                    )
                                    continuation.resume(
                                        runBlocking {
                                            accountService.signInWithGoogle(
                                                String(decryptedData)
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
                        .setTitle("Biometric authentication")
                        .setSubtitle("Please authenticate yourself to continue")
                        .setNegativeButtonText("Cancel")
                        .build()

                val cipher = getCipher()
                val secretKey = getSecretKey()
                cipher.init(Cipher.DECRYPT_MODE, secretKey)

                biometricPrompt.authenticate(
                    BiometricPrompt.CryptoObject(cipher),
                    promptInfo
                )
            }

        override fun saveUserToFirestore(
            userId: String,
            email: String,
            displayName: String,
            profilePictureUrl: String?,
        ) {
            val membership = Membership()
            firestore
                .collection("memberships")
                .document(userId)
                .set(membership)
                .addOnSuccessListener {
                    if (profilePictureUrl != null) {
                        val profilePictureData =
                            ProfilePicture(
                                image = profilePictureUrl,
                                type = "url",
                            )
                        firestore
                            .collection("profilePictures")
                            .document(userId)
                            .set(profilePictureData)
                            .addOnSuccessListener { saveUserDocument(userId, email, displayName) }
                            .addOnFailureListener { }
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
                        firestore
                            .collection("notificationSettings")
                            .document(userId)
                            .set(notificationSettings)
                            .addOnFailureListener { }
                    } else {
                        saveUserDocument(userId, email, displayName)
                    }
                }.addOnFailureListener { }
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
            context: Context,
            activity: FragmentActivity,
            onSuccess: () -> Unit,
            onError: (Int) -> Unit,
        ) {
            val isBiometricEnabled = runBlocking { accountService.getBiometricEnabled() }

            if (isBiometricEnabled) {
                if (authenticateBiometric(context, activity, credential)) {
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

package com.freshkeeper.model.service

import android.util.Log
import com.freshkeeper.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Locale
import javax.inject.Inject

class AccountServiceImpl
    @Inject
    constructor() : AccountService {
        init {
            val languageCode = Locale.getDefault().language
            if (languageCode.isNotEmpty()) {
                FirebaseAuth.getInstance().setLanguageCode(languageCode)
            } else {
                Log.e("AccountServiceImpl", "Language code is empty or null")
            }
        }

        override val currentUser: Flow<User?>
            get() =
                callbackFlow {
                    val listener =
                        FirebaseAuth.AuthStateListener { auth ->
                            val firebaseUser = auth.currentUser
                            if (firebaseUser?.isAnonymous == true && firebaseUser.email == null) {
                                trySend(null)
                            } else {
                                trySend(firebaseUser.toUser())
                            }
                        }
                    Firebase.auth.addAuthStateListener(listener)
                    awaitClose { Firebase.auth.removeAuthStateListener(listener) }
                }

        override val currentUserId: String
            get() =
                Firebase.auth.currentUser
                    ?.uid
                    .orEmpty()

        override fun hasUser(): Boolean = Firebase.auth.currentUser != null

        override fun getUserProfile(): User = Firebase.auth.currentUser.toUser()

        override suspend fun createAnonymousAccount() {
            Firebase.auth.signInAnonymously().await()
        }

        override suspend fun updateDisplayName(newDisplayName: String) {
            val profileUpdates =
                userProfileChangeRequest {
                    displayName = newDisplayName
                }

            Firebase.auth.currentUser!!
                .updateProfile(profileUpdates)
                .await()
        }

        override suspend fun linkAccountWithEmail(
            email: String,
            password: String,
        ) {
            val credential = EmailAuthProvider.getCredential(email, password)
            Firebase.auth.currentUser!!
                .linkWithCredential(credential)
                .await()
        }

        override suspend fun signInWithGoogle(idToken: String) {
            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            Firebase.auth.signInWithCredential(firebaseCredential).await()
        }

        override suspend fun signUpWithEmail(
            email: String,
            password: String,
        ) {
            // linkAccountWithEmail(email, password)
            Firebase.auth.createUserWithEmailAndPassword(email, password).await()
        }

        override suspend fun signInWithEmail(
            email: String,
            password: String,
        ) {
            try {
                val authResult = Firebase.auth.signInWithEmailAndPassword(email, password).await()
                val firebaseUser = authResult.user

                if (firebaseUser != null && !firebaseUser.isEmailVerified) {
                    throw Exception("Email address not verified. Please verify your email before signing in.")
                }

                Log.d("AccountServiceImpl", "Sign-in successful for user: ${firebaseUser?.email}")
            } catch (e: Exception) {
                Log.e("AccountServiceImpl", "Sign-in failed", e)
                throw e
            }
        }

        override suspend fun signOut() {
            Firebase.auth.signOut()
            Log.d("AccountServiceImpl", "User signed out")
        }

        override suspend fun changeEmail(newEmail: String) {
            try {
                Firebase.auth.currentUser
                    ?.verifyBeforeUpdateEmail(newEmail)
                    ?.await()
                Log.d("AccountServiceImpl", "Email address updated to $newEmail")
            } catch (e: Exception) {
                Log.e("AccountServiceImpl", "Failed to update email address", e)
                throw e
            }
        }

        override suspend fun resetPassword() {
            Firebase.auth.sendPasswordResetEmail(Firebase.auth.currentUser!!.email!!).await()
        }

        override suspend fun forgotPassword(email: String) {
            Firebase.auth.sendPasswordResetEmail(email).await()
        }

        override suspend fun deleteAccount() {
            try {
                Firebase.auth.currentUser
                    ?.delete()
                    ?.await()
                Firebase.auth.signOut()
                Log.d("AccountServiceImpl", "User account deleted and signed out")
            } catch (e: Exception) {
                Log.e("AccountServiceImpl", "Error deleting account", e)
                throw e
            }
        }

        private fun FirebaseUser?.toUser(): User =
            if (this == null) {
                User()
            } else {
                User(
                    id = this.uid,
                    email = this.email ?: "",
                    provider = this.providerId,
                    displayName = this.displayName ?: "",
                    isAnonymous = this.isAnonymous,
                    isEmailVerified = this.isEmailVerified,
                )
            }

        override suspend fun sendEmailVerification() {
            try {
                val user = Firebase.auth.currentUser
                user?.reload()?.await()

                if (user != null && user.isEmailVerified) {
                    Log.d("AccountServiceImpl", "Email is already verified")
                }

                if (user != null && !user.isEmailVerified) {
                    user.sendEmailVerification().await()
                    Log.d("AccountServiceImpl", "Email verification sent")
                }
            } catch (e: Exception) {
                Log.e("AccountServiceImpl", "Failed to send email verification", e)
                throw e
            }
        }
    }

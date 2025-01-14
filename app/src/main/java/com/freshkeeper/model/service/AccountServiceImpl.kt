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
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Locale
import javax.inject.Inject

class AccountServiceImpl
    @Inject
    constructor() : AccountService {
        private val auth: FirebaseAuth = Firebase.auth
        private val firestore = Firebase.firestore

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
                auth.currentUser
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

            auth.currentUser!!
                .updateProfile(profileUpdates)
                .await()
        }

        override suspend fun linkAccountWithEmail(
            email: String,
            password: String,
        ) {
            val credential = EmailAuthProvider.getCredential(email, password)
            auth.currentUser!!
                .linkWithCredential(credential)
                .await()
        }

        override suspend fun signInWithGoogle(idToken: String) {
            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(firebaseCredential).await()
        }

        override suspend fun signUpWithEmail(
            email: String,
            password: String,
        ) {
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
            auth.signOut()
            Log.d("AccountServiceImpl", "User signed out")
        }

        override suspend fun changeEmail(newEmail: String) {
            try {
                auth.currentUser
                    ?.verifyBeforeUpdateEmail(newEmail)
                    ?.await()
                Log.d("AccountServiceImpl", "Email address updated to $newEmail")
            } catch (e: Exception) {
                Log.e("AccountServiceImpl", "Failed to update email address", e)
                throw e
            }
        }

        override suspend fun resetPassword() {
            auth.sendPasswordResetEmail(auth.currentUser!!.email!!).await()
        }

        override suspend fun forgotPassword(email: String) {
            auth.sendPasswordResetEmail(email).await()
        }

        override suspend fun deleteAccount() {
            try {
                Firebase.auth.currentUser
                    ?.delete()
                    ?.await()
                auth.signOut()
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
                    profilePicture = null,
                    isAnonymous = this.isAnonymous,
                    isEmailVerified = this.isEmailVerified,
                    isBiometricEnabled = false,
                    createdAt = 0,
                    householdId = "",
                )
            }

        override suspend fun sendEmailVerification() {
            try {
                val user = auth.currentUser
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

        override suspend fun getHouseholdId(): String {
            val currentUser = auth.currentUser ?: return ""

            val userRef = firestore.collection("users").document(currentUser.uid)
            return try {
                val documentSnapshot = userRef.get().await()
                val householdId = documentSnapshot.getString("householdId")
                householdId.orEmpty()
            } catch (e: Exception) {
                Log.e("AccountServiceImpl", "Error fetching householdId for user", e)
                ""
            }
        }

        override suspend fun updateProfilePicture(base64Image: String) {
            val userId = auth.currentUser?.uid ?: throw Exception("User is not logged in")

            try {
                val userRef = firestore.collection("users").document(userId)
                val userDoc = userRef.get().await()
                val oldProfilePictureId = userDoc.getString("profilePicture")

                if (!oldProfilePictureId.isNullOrEmpty()) {
                    val oldProfilePictureRef =
                        firestore
                            .collection("profilePictures")
                            .document(oldProfilePictureId)
                    oldProfilePictureRef.delete().await()
                }

                val profilePictureRef = firestore.collection("profilePictures").document()
                profilePictureRef.set(mapOf("image" to base64Image)).await()

                val profilePictureId = profilePictureRef.id

                userRef.update("profilePicture", profilePictureId).await()
            } catch (e: Exception) {
                Log.e("AccountServiceImpl", "Error updating profile picture", e)
                throw e
            }
        }

        override suspend fun getProfilePicture(): String? {
            val userId = auth.currentUser?.uid ?: return null
            val userRef = firestore.collection("users").document(userId)
            val userSnapshot = userRef.get().await()

            val profilePictureId = userSnapshot.getString("profilePicture")

            if (profilePictureId != null) {
                val pictureRef = firestore.collection("profilePictures").document(profilePictureId)
                val pictureSnapshot = pictureRef.get().await()
                return pictureSnapshot.getString("image")
            }

            return null
        }
    }

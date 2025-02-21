package com.freshkeeper.service

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import com.freshkeeper.R
import com.freshkeeper.model.Activity
import com.freshkeeper.model.DownloadableUserData
import com.freshkeeper.model.FoodItem
import com.freshkeeper.model.Household
import com.freshkeeper.model.ProfilePicture
import com.freshkeeper.model.User
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.firestore
import com.google.gson.Gson
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.io.File
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
                    auth.addAuthStateListener(listener)
                    awaitClose { auth.removeAuthStateListener(listener) }
                }

        override fun hasUser(): Boolean = auth.currentUser != null

        override fun getUserProfile(): User = auth.currentUser.toUser()

        override fun getEmailForCurrentUser(): String {
            val currentUser = auth.currentUser
            return currentUser?.email
                ?: throw IllegalStateException("Current user is not logged in or email is unavailable")
        }

        override suspend fun getUserObject(): User {
            val currentUser = auth.currentUser
            return if (currentUser != null) {
                val userId = currentUser.uid
                val userDocumentRef = firestore.collection("users").document(userId)
                val userSnapshot = userDocumentRef.get().await()
                userSnapshot.toObject(User::class.java) ?: User()
            } else {
                User()
            }
        }

        override suspend fun createAnonymousAccount() {
            auth.signInAnonymously().await()
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
            auth.createUserWithEmailAndPassword(email, password).await()
        }

        override suspend fun signInWithEmail(
            email: String,
            password: String,
        ) {
            try {
                val authResult = auth.signInWithEmailAndPassword(email, password).await()
                val firebaseUser = authResult.user

                if (firebaseUser != null && !firebaseUser.isEmailVerified) {
                    throw Exception(
                        "Email address not verified. " +
                            "Please verify your email before signing in.",
                    )
                }
            } catch (e: Exception) {
                Log.e("AccountServiceImpl", "Sign-in failed", e)
                throw e
            }
        }

        override suspend fun signOut() {
            auth.signOut()
        }

        override suspend fun changeEmail(newEmail: String) {
            try {
                auth.currentUser
                    ?.verifyBeforeUpdateEmail(newEmail)
                    ?.await()
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
                val currentUser = auth.currentUser
                val user = getUserObject()
                val userId = user.id

                if (userId.isNotEmpty()) {
                    firestore
                        .collection("profilePictures")
                        .document(userId)
                        .delete()
                        .await()

                    firestore
                        .collection("notificationSettings")
                        .document(userId)
                        .delete()
                        .await()

                    firestore
                        .collection("memberships")
                        .document(userId)
                        .delete()
                        .await()

                    deleteLinkedDocuments(userId)

                    firestore
                        .collection("users")
                        .document(userId)
                        .delete()
                        .await()

                    auth.signOut()
                    currentUser?.delete()?.await()
                }
            } catch (e: FirebaseAuthRecentLoginRequiredException) {
                Log.e("AccountServiceImpl", "User needs to reauthenticate", e)
            } catch (e: Exception) {
                Log.e("AccountServiceImpl", "Error deleting account", e)
                throw e
            }
        }

        private suspend fun deleteLinkedDocuments(userId: String) {
            val householdDocuments =
                firestore
                    .collection("households")
                    .whereEqualTo("ownerId", userId)
                    .get()
                    .await()

            householdDocuments.documents.forEach { household ->
                firestore
                    .collection("households")
                    .document(household.id)
                    .delete()
                    .await()
            }

            val collections =
                listOf(
                    "foodItems",
                    "activities",
                )
            collections.forEach { collection ->
                val documents =
                    firestore
                        .collection(collection)
                        .whereEqualTo("userId", userId)
                        .get()
                        .await()

                documents.documents.forEach { document ->
                    firestore
                        .collection(collection)
                        .document(document.id)
                        .delete()
                        .await()
                }
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
                    isBiometricEnabled = false,
                    createdAt = 0,
                    householdId = "",
                )
            }

        override suspend fun sendEmailVerification() {
            try {
                val user = auth.currentUser
                user?.reload()?.await()

                if (user != null) {
                    val isVerified = user.isEmailVerified

                    firestore
                        .collection("users")
                        .document(user.uid)
                        .update("isEmailVerified", isVerified)
                        .addOnFailureListener { e ->
                            Log.e(
                                "AccountServiceImpl",
                                "Error updating isEmailVerified: ${e.message}",
                                e,
                            )
                        }

                    if (!isVerified) {
                        user.sendEmailVerification().await()
                    } else {
                        Log.e("AccountServiceImpl", "Email is already verified")
                    }
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
                val profilePictureRef =
                    firestore
                        .collection("profilePictures")
                        .document(userId)

                profilePictureRef
                    .set(ProfilePicture(base64Image, "base64"))
                    .await()
            } catch (e: Exception) {
                Log.e("AccountServiceImpl", "Error updating profile picture", e)
                throw e
            }
        }

        override suspend fun getProfilePicture(userId: String): ProfilePicture? =
            try {
                val pictureRef =
                    firestore
                        .collection("profilePictures")
                        .document(userId)
                val pictureSnapshot = pictureRef.get().await()

                pictureSnapshot.toObject(ProfilePicture::class.java)
            } catch (e: Exception) {
                Log.e(
                    "AccountService",
                    "Error retrieving profile picture for userId: $userId",
                    e,
                )
                null
            }

        override suspend fun downloadUserData(
            userId: String,
            context: Context,
        ) {
            val fileName = "user_data.json"

            firestore
                .collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { userDoc ->
                    if (userDoc != null) {
                        val user = userDoc.toObject(User::class.java)
                        if (user != null) {
                            val activitiesTask =
                                firestore
                                    .collection("activities")
                                    .whereEqualTo("userId", userId)
                                    .get()

                            val foodItemsTask =
                                firestore
                                    .collection("foodItems")
                                    .whereEqualTo("userId", userId)
                                    .get()

                            val householdTask =
                                user.householdId?.let { householdId ->
                                    firestore
                                        .collection("households")
                                        .document(householdId)
                                        .get()
                                }

                            Tasks
                                .whenAllComplete(activitiesTask, foodItemsTask, householdTask)
                                .addOnCompleteListener {
                                    val activities =
                                        activitiesTask.result?.toObjects(
                                            Activity::class.java,
                                        ) ?: emptyList()
                                    val foodItems =
                                        foodItemsTask.result?.toObjects(
                                            FoodItem::class.java,
                                        ) ?: emptyList()
                                    val household =
                                        householdTask?.result?.toObject(
                                            Household::class.java,
                                        )

                                    val downloadableData =
                                        DownloadableUserData(
                                            user,
                                            activities,
                                            foodItems,
                                            household,
                                        )

                                    val jsonString = Gson().toJson(downloadableData)

                                    val file = File(context.cacheDir, fileName)
                                    file.writeText(jsonString)

                                    val uri =
                                        FileProvider.getUriForFile(
                                            context,
                                            "${context.packageName}.provider",
                                            file,
                                        )

                                    val intent =
                                        Intent(Intent.ACTION_SEND).apply {
                                            type = "application/json"
                                            putExtra(Intent.EXTRA_STREAM, uri)
                                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }
                                    context.startActivity(
                                        Intent.createChooser(
                                            intent,
                                            context.getString(R.string.download_data_title),
                                        ),
                                    )
                                }
                        }
                    }
                }.addOnFailureListener {
                    Toast
                        .makeText(
                            context,
                            context.getString(R.string.download_data_error),
                            Toast.LENGTH_SHORT,
                        ).show()
                }
        }
    }

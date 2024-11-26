package com.freshkeeper.model.service

import android.util.Log
import com.freshkeeper.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AccountServiceImpl
    @Inject
    constructor() : AccountService {
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

        override suspend fun linkAccountWithGoogle(idToken: String) {
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                FirebaseAuth.getInstance().signInWithCredential(credential).await()
                Log.d("FirebaseAuth", "Google account successfully linked.")
            } catch (e: Exception) {
                Log.e("FirebaseAuth", "Error linking Google account: ${e.message}", e)
                throw e
            }
        }

        override suspend fun linkPasswordToGoogleAccount(
            email: String,
            password: String,
        ) {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                val credential = EmailAuthProvider.getCredential(email, password)
                currentUser
                    .linkWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            println("Password successfully linked to Google account.")
                        } else {
                            val exception = task.exception
                            if (exception is FirebaseAuthUserCollisionException) {
                                println("Account with this email address already exists.")
                            } else {
                                println("Error: ${exception?.localizedMessage}")
                            }
                        }
                    }
            } else {
                println("Error: No user is logged in.")
            }
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

        override suspend fun signInWithEmail(
            email: String,
            password: String,
        ) {
            Firebase.auth.signInWithEmailAndPassword(email, password).await()
        }

        override suspend fun signOut() {
            Firebase.auth.signOut()
            Log.d("AccountServiceImpl", "User signed out")
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
                )
            }

        override suspend fun sendEmailVerification() {
            val user = Firebase.auth.currentUser
            user?.sendEmailVerification()?.await()
        }
    }

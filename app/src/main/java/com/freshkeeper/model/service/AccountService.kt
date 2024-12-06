package com.freshkeeper.model.service

import com.freshkeeper.model.User
import kotlinx.coroutines.flow.Flow

interface AccountService {
    val currentUser: Flow<User?>
    val currentUserId: String

    fun hasUser(): Boolean

    fun getUserProfile(): User

    suspend fun createAnonymousAccount()

    suspend fun updateDisplayName(newDisplayName: String)

    suspend fun linkAccountWithEmail(
        email: String,
        password: String,
    )

    suspend fun signInWithGoogle(idToken: String)

    suspend fun signUpWithEmail(
        email: String,
        password: String,
    )

    suspend fun signInWithEmail(
        email: String,
        password: String,
    )

    suspend fun signOut()

    suspend fun changeEmail(newEmail: String)

    suspend fun resetPassword()

    suspend fun forgotPassword(email: String)

    suspend fun deleteAccount()

    suspend fun sendEmailVerification()
}

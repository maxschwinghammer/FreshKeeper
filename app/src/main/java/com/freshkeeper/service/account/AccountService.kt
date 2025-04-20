package com.freshkeeper.service.account

import com.freshkeeper.model.ProfilePicture
import com.freshkeeper.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface AccountService {
    val currentUser: Flow<User?>
    val logoutEvents: SharedFlow<Unit>

    fun hasUser(): Boolean

    fun getUserProfile(): User

    suspend fun getUserObject(): User

    suspend fun getUserProfile(userId: String): User

    suspend fun calculateDaysSince(createdAt: Long): Long

    fun getEmailForCurrentUser(): String

    suspend fun getBiometricEnabled(): Boolean

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

    suspend fun updateProfilePicture(base64Image: String)

    suspend fun getProfilePicture(userId: String): ProfilePicture?

    suspend fun downloadUserData(userId: String)

    suspend fun saveUserToFirestore(
        userId: String,
        email: String,
    )
}

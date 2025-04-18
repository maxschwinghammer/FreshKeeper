package com.freshkeeper.service.googleAccount

import androidx.credentials.Credential
import androidx.fragment.app.FragmentActivity

interface GoogleAccountService {
    suspend fun authenticateBiometric(
        activity: FragmentActivity,
        credential: Credential?,
    ): Boolean

    suspend fun signInWithGoogle(
        credential: Credential,
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onError: (Int) -> Unit,
    )

    fun saveUserToFirestore(
        userId: String,
        email: String,
        displayName: String,
        profilePictureUrl: String?,
    )

    fun saveUserDocument(
        userId: String,
        email: String,
        displayName: String,
    )
}

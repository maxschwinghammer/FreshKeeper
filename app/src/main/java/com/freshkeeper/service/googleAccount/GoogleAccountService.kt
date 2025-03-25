package com.freshkeeper.service.googleAccount

import android.content.Context
import androidx.credentials.Credential
import androidx.fragment.app.FragmentActivity

interface GoogleAccountService {
    suspend fun authenticateBiometric(
        context: Context,
        activity: FragmentActivity,
        credential: Credential?,
    ): Boolean

    suspend fun signInWithGoogle(
        credential: Credential,
        context: Context,
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

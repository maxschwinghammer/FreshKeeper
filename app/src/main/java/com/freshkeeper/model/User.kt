package com.freshkeeper.model

import com.google.firebase.firestore.PropertyName

data class User(
    val id: String = "",
    val email: String = "",
    val provider: String = "",
    val displayName: String? = null,
    val profilePicture: String? = null,
    @get:PropertyName("isAnonymous")
    val isAnonymous: Boolean = false,
    @get:PropertyName("isEmailVerified")
    val isEmailVerified: Boolean = false,
    @get:PropertyName("isBiometricEnabled")
    val isBiometricEnabled: Boolean = false,
    val createdAt: Long = 0,
    val householdId: String? = null,
    val membershipId: String? = null,
)

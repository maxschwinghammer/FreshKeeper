package com.freshkeeper.model

data class User(
    val id: String = "",
    val email: String = "",
    val provider: String = "",
    val displayName: String? = null,
    val profilePicture: String? = null,
    val isAnonymous: Boolean = true,
    val isEmailVerified: Boolean = false,
    val isBiometricEnabled: Boolean = false,
    val createdAt: Long = 0,
    val householdId: String? = null,
)

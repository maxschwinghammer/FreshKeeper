package com.freshkeeper.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName

data class User(
    val id: String = "",
    val email: String = "",
    val provider: String = "",
    val displayName: String? = null,
    @get:Exclude @set:Exclude
    var isAnonymous: Boolean = false,
    @get:Exclude @set:Exclude
    var isEmailVerified: Boolean = false,
    @get:PropertyName("isBiometricEnabled")
    val isBiometricEnabled: Boolean = false,
    val createdAt: Long = 0,
    val householdId: String? = null,
)

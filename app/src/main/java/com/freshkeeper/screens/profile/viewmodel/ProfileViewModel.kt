package com.freshkeeper.screens.profile.viewmodel

import com.freshkeeper.model.ProfilePicture
import com.freshkeeper.model.User
import com.freshkeeper.screens.AppViewModel
import com.freshkeeper.service.AccountService
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel
    @Inject
    constructor(
        private val accountService: AccountService,
        private val firestore: FirebaseFirestore,
    ) : AppViewModel() {
        private val _user = MutableStateFlow<User?>(null)
        val user: StateFlow<User?> = _user.asStateFlow()

        private val _memberSinceDays = MutableStateFlow<Long>(0)
        val memberSinceDays: StateFlow<Long> = _memberSinceDays.asStateFlow()

        private val _profilePicture = MutableStateFlow<ProfilePicture?>(null)
        val profilePicture: StateFlow<ProfilePicture?> = _profilePicture.asStateFlow()

        fun loadUserProfile(userId: String) {
            launchCatching {
                val snapshot =
                    firestore
                        .collection("users")
                        .document(userId)
                        .get()
                        .await()
                _user.value = snapshot.toObject(User::class.java)

                val createdAt = snapshot.getLong("createdAt") ?: 0L
                _memberSinceDays.value = calculateDaysSince(createdAt)

                _profilePicture.value = accountService.getProfilePicture(userId)
            }
        }

        fun saveName(
            userId: String,
            name: String,
        ) {
            _user.value = _user.value?.copy(displayName = name)
            launchCatching {
                firestore
                    .collection("users")
                    .document(userId)
                    .update("displayName", name)
                    .await()
            }
        }

        private fun calculateDaysSince(createdAt: Long): Long {
            val creationDate = Instant.ofEpochMilli(createdAt).atZone(ZoneId.systemDefault()).toLocalDate()
            val currentDate = LocalDate.now()
            return ChronoUnit.DAYS.between(creationDate, currentDate)
        }
    }

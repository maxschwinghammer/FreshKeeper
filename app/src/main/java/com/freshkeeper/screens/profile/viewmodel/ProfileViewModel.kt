package com.freshkeeper.screens.profile.viewmodel

import com.freshkeeper.model.User
import com.freshkeeper.model.service.AccountService
import com.freshkeeper.screens.AppViewModel
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

        private val _profilePictureBase64 = MutableStateFlow<String?>(null)
        val profilePictureBase64: StateFlow<String?> = _profilePictureBase64.asStateFlow()

        init {
            launchCatching {
                val userProfile = accountService.getUserProfile()
                _user.value = userProfile
                loadMemberSinceDays(userProfile.id)
                _profilePictureBase64.value = accountService.getProfilePicture()
            }
        }

        private suspend fun loadMemberSinceDays(userId: String) {
            val snapshot =
                firestore
                    .collection("users")
                    .document(userId)
                    .get()
                    .await()
            val createdAt = snapshot.getLong("createdAt") ?: 0L
            _memberSinceDays.value = calculateDaysSince(createdAt)
        }

        private fun calculateDaysSince(createdAt: Long): Long {
            val creationDate = Instant.ofEpochMilli(createdAt).atZone(ZoneId.systemDefault()).toLocalDate()
            val currentDate = LocalDate.now()
            return ChronoUnit.DAYS.between(creationDate, currentDate)
        }
    }

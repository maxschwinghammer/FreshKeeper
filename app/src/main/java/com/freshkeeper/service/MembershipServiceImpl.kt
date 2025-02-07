package com.freshkeeper.service

import com.freshkeeper.model.Membership
import com.freshkeeper.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MembershipServiceImpl
    @Inject
    constructor() : MembershipService {
        private val firestore = FirebaseFirestore.getInstance()
        private val userId = FirebaseAuth.getInstance().currentUser?.uid

        override suspend fun getMembershipStatus(): Membership {
            return try {
                if (userId == null) return Membership()
                val userDoc =
                    firestore
                        .collection("users")
                        .document(userId)
                        .get()
                        .await()
                val user = userDoc.toObject(User::class.java)
                val membershipId = user?.membershipId
                if (membershipId.isNullOrEmpty()) return Membership()
                val membershipDoc =
                    firestore
                        .collection("memberships")
                        .document(membershipId)
                        .get()
                        .await()
                membershipDoc.toObject(Membership::class.java) ?: Membership()
            } catch (e: Exception) {
                Membership()
            }
        }

        override suspend fun activateMembership(
            paymentCycle: String,
            durationInDays: Int,
        ) {
            try {
                if (userId == null) return
                val userDocRef = firestore.collection("users").document(userId)
                val userDoc = userDocRef.get().await()
                val user = userDoc.toObject(User::class.java) ?: return

                val membershipId =
                    user.membershipId ?: firestore
                        .collection("memberships")
                        .document()
                        .id
                val startDate = System.currentTimeMillis()
                val endDate = startDate + durationInDays * 24 * 60 * 60 * 1000L

                val updatedMembership =
                    Membership(
                        userId = userId,
                        id = membershipId,
                        hasPremium = true,
                        hasTested = true,
                        paymentCycle = paymentCycle,
                        startDate = startDate,
                        endDate = endDate,
                    )

                firestore
                    .collection("memberships")
                    .document(membershipId)
                    .set(updatedMembership)
                    .await()
                userDocRef.update("membershipId", membershipId).await()
            } catch (e: Exception) {
                // Fehlerhandling, z. B. Logging
            }
        }
    }

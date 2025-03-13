package com.freshkeeper.service

import android.util.Log
import com.freshkeeper.model.Membership
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
                if (userId.isNullOrEmpty()) return Membership()
                val membershipDoc =
                    firestore
                        .collection("memberships")
                        .document(userId)
                        .get()
                        .await()
                membershipDoc.toObject(Membership::class.java) ?: Membership()
            } catch (e: Exception) {
                Membership()
            }
        }

        override suspend fun isMember(): Boolean =
            try {
                val membership = getMembershipStatus()
                membership.hasPremium && (membership.endDate ?: 0) > System.currentTimeMillis()
            } catch (e: Exception) {
                false
            }

        override suspend fun activateMembership(
            paymentCycle: String,
            durationInDays: Int,
        ) {
            try {
                if (userId == null) return

                val startDate = System.currentTimeMillis()
                val endDate = startDate + durationInDays * 24 * 60 * 60 * 1000L

                val updatedMembership =
                    Membership(
                        hasPremium = true,
                        hasTested = true,
                        paymentCycle = paymentCycle,
                        startDate = startDate,
                        endDate = endDate,
                    )

                firestore
                    .collection("memberships")
                    .document(userId)
                    .set(updatedMembership)
                    .await()
            } catch (e: Exception) {
                Log.e("MembershipService", "Error activating membership", e)
            }
        }
    }

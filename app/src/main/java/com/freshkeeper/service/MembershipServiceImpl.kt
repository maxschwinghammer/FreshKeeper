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
    }

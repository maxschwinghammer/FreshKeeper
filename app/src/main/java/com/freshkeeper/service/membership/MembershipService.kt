package com.freshkeeper.service.membership

import com.freshkeeper.model.Membership

interface MembershipService {
    suspend fun getMembershipStatus(): Membership

    suspend fun isMember(): Boolean

    suspend fun activateMembership(
        paymentCycle: String,
        durationInDays: Int,
    )
}

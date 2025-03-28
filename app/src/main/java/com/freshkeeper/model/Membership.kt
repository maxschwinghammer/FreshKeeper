package com.freshkeeper.model

data class Membership(
    val hasPremium: Boolean = false,
    val hasTested: Boolean = false,
    val paymentCycle: String? = null,
    val startDate: Long? = null,
    val endDate: Long? = null,
)

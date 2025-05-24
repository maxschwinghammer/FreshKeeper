package com.freshkeeper.model

data class Household(
    val id: String = "",
    val type: HouseholdType = HouseholdType.SINGLE,
    val users: List<String> = emptyList(),
    val name: String = "",
    val createdAt: Long = 0,
    val ownerId: String = "",
)

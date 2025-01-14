package com.freshkeeper.model

data class Household(
    val id: String = "",
    val type: String = "",
    val users: List<String> = emptyList(),
    val name: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val invites: List<String> = emptyList(),
    val ownerId: String = "",
)

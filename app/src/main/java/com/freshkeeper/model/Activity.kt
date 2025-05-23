package com.freshkeeper.model

data class Activity(
    val id: String? = null,
    val userId: String,
    val householdId: String? = null,
    val type: EventType,
    val userName: String,
    val timestamp: Long,
    val oldProductName: String? = null,
    val productName: String? = null,
    val deleted: Boolean = false,
) {
    constructor() : this("", "", "", EventType.EDIT, "", 0, "", "", false)
}

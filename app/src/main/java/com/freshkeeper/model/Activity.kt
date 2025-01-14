package com.freshkeeper.model

data class Activity(
    val id: String? = null,
    val userId: String,
    val householdId: String? = null,
    val type: String,
    val text: String,
    val timestamp: Long,
    val deleted: Boolean = false,
) {
    constructor() : this(
        "",
        "",
        "",
        "",
        "",
        0,
        false,
    )
}

package com.freshkeeper.model

data class FoodItem(
    val id: Long,
    val userId: String,
    val householdId: String,
    val name: String,
    val expiryTimestamp: Long,
    val quantity: Int,
    val unit: String,
    val storageLocation: String,
    val category: String,
    val consumed: Boolean,
    val thrownAway: Boolean,
    val imageUrl: String,
    var daysDifference: Int = 0,
) {
    constructor() : this(
        0,
        "",
        "",
        "",
        0L,
        0,
        "",
        "",
        "",
        false,
        false,
        "",
    )
}

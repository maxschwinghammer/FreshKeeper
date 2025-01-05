package com.freshkeeper.screens.home.viewmodel

data class FoodItem(
    val id: Long,
    val userId: String,
    val name: String,
    val expiryTimestamp: Long,
    val quantity: Int,
    val unit: String,
    val storageLocation: Int,
    val category: Int,
    val consumed: Boolean,
    val thrownAway: Boolean,
    val imageUrl: String,
    var daysDifference: Int = 0,
) {
    constructor() : this(
        0,
        "",
        "",
        0L,
        0,
        "",
        0,
        0,
        false,
        false,
        "",
    )
}

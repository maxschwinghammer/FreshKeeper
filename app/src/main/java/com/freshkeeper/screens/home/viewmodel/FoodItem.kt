package com.freshkeeper.screens.home.viewmodel

data class FoodItem(
    val id: Long,
    val name: String,
    val expiryTimestamp: Long,
    val quantity: Int,
    val unit: String,
    val storageLocation: Int,
    val category: Int,
    val isConsumed: Boolean,
    val isThrownAway: Boolean,
    val imageUrl: String,
    var daysDifference: Int = 0,
)

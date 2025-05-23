package com.freshkeeper.model

import com.google.firebase.firestore.Exclude

data class FoodItem(
    val id: String? = null,
    val barcode: String? = null,
    val userId: String,
    val householdId: String? = null,
    val name: String,
    val expiryTimestamp: Long,
    val discardTimestamp: Long? = null,
    val quantity: Int,
    val unit: String,
    val storageLocation: String,
    val category: String,
    val status: FoodStatus,
    val imageId: String? = null,
    @get:Exclude @set:Exclude @Transient var daysDifference: Int = 0,
    val nutriments: Nutriments? = null,
    val nutriScore: String? = null,
) {
    constructor() : this(
        "",
        null,
        "",
        "",
        "",
        0L,
        0L,
        0,
        "",
        "",
        "",
        FoodStatus.ACTIVE,
        null,
        0,
        Nutriments(),
        "",
    )
}

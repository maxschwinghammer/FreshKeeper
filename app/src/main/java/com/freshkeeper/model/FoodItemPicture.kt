package com.freshkeeper.model

data class FoodItemPicture(
    val image: String? = null,
    val type: String? = null,
) {
    constructor() : this(null, null)
}

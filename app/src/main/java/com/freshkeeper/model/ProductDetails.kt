package com.freshkeeper.model

data class ProductDetails(
    val productName: String? = null,
    val brand: String? = null,
    val nutriScore: String? = null,
    val ingredients: String? = null,
    val labels: List<String>? = null,
    val vegan: Boolean? = null,
    val vegetarian: Boolean? = null,
    val organic: Boolean? = null,
    val nutriments: Nutriments? = null,
)

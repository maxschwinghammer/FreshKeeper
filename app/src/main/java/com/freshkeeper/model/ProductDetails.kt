package com.freshkeeper.model

data class ProductDetails(
    val productName: String?,
    val brand: String?,
    val nutriScore: String?,
    val ingredients: String?,
    val labels: List<String>?,
    val isVegan: Boolean?,
    val isVegetarian: Boolean?,
    val isOrganic: Boolean?,
    val nutriments: Nutriments?,
)

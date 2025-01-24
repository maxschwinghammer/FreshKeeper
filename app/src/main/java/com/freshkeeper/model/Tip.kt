package com.freshkeeper.model

data class Tip(
    val titleId: Int,
    val descriptionId: Int,
    val imageResId: Int,
    val householdId: String? = null,
)

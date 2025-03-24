package com.freshkeeper.model

data class Nutriments(
    val energyKcal: Double? = null,
    val fat: Double? = null,
    val carbohydrates: Double? = null,
    val sugars: Double? = null,
    val fiber: Double? = null,
    val proteins: Double? = null,
    val salt: Double? = null,
) {
    fun isEmpty(): Boolean =
        energyKcal == null &&
            fat == null &&
            carbohydrates == null &&
            sugars == null &&
            fiber == null &&
            proteins == null &&
            salt == null
}

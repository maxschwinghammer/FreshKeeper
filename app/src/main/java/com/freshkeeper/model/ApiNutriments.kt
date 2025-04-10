package com.freshkeeper.model

import com.google.gson.annotations.SerializedName

data class ApiNutriments(
    @SerializedName("energy-kcal")
    val energyKcal: Double?,
    val fat: Double?,
    val carbohydrates: Double?,
    val sugars: Double?,
    val fiber: Double?,
    val proteins: Double?,
    val salt: Double?,
)

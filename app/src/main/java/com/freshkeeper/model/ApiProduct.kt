package com.freshkeeper.model

import com.google.gson.annotations.SerializedName

data class ApiProduct(
    @SerializedName("product_name")
    val productName: String?,
    val brands: String?,
    @SerializedName("nutriscore_grade")
    val nutriscoreGrade: String?,
    @SerializedName("ingredients_text")
    val ingredientsText: String?,
    @SerializedName("labels_tags")
    val labelsTags: List<String>?,
    val nutriments: ApiNutriments?,
)

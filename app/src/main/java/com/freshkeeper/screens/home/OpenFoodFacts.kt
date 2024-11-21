package com.freshkeeper.screens.home

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

suspend fun fetchProductDataFromBarcode(barcode: String): ProductData? {
    return withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        val url = "https://world.openfoodfacts.org/api/v3/product/$barcode.json"
        val request = Request.Builder().url(url).build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return@withContext null
            val responseData = response.body.string()
            val json = JSONObject(responseData)
            val productJson = json.optJSONObject("product")

            val name = productJson?.optString("product_name")
            val quantityWithUnit = productJson?.optString("quantity")
            val (quantity, unit) =
                if (quantityWithUnit.isNullOrEmpty()) {
                    Pair("1", "pckg.")
                } else {
                    splitQuantityAndUnit(quantityWithUnit)
                }

            val imageUrl = productJson?.optString("image_url")

            ProductData(name, quantity, unit, imageUrl)
        }
    }
}

fun splitQuantityAndUnit(quantityWithUnit: String): Pair<String, String> {
    val match = Regex("^(\\d+)(.*)$").find(quantityWithUnit.trim())
    val quantity = match?.groups?.get(1)?.value ?: ""
    val unit =
        match
            ?.groups
            ?.get(2)
            ?.value
            ?.trim() ?: ""
    return Pair(quantity, unit)
}

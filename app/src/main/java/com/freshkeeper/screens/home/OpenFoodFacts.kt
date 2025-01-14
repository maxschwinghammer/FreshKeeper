package com.freshkeeper.screens.home

import android.content.Context
import android.widget.Toast
import com.freshkeeper.model.ProductData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.SocketTimeoutException

suspend fun fetchProductDataFromBarcode(
    context: Context,
    barcode: String,
): ProductData? {
    return withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        val url = "https://world.openfoodfacts.org/api/v3/product/$barcode.json"
        val request = Request.Builder().url(url).build()

        try {
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

                return@withContext ProductData(name, quantity, unit, imageUrl)
            }
        } catch (e: SocketTimeoutException) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Network error: Timeout", Toast.LENGTH_SHORT).show()
            }
            return@withContext null
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast
                    .makeText(
                        context,
                        "Network error: ${e.message}",
                        Toast.LENGTH_SHORT,
                    ).show()
            }
            return@withContext null
        }
    }
}

fun splitQuantityAndUnit(quantityWithUnit: String): Pair<String, String> {
    val cleanedQuantityWithUnit =
        quantityWithUnit
            .replace(Regex("\\(.*?\\)"), "")
            .trim()
    val match = Regex("^(\\d+)(.*)$").find(cleanedQuantityWithUnit)
    val quantity = match?.groups?.get(1)?.value ?: ""
    val unit =
        match
            ?.groups
            ?.get(2)
            ?.value
            ?.trim() ?: ""
    return Pair(quantity, unit)
}

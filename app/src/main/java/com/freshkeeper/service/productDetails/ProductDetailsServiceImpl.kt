package com.freshkeeper.service.productDetails

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import com.freshkeeper.model.ApiResponse
import com.freshkeeper.model.Nutriments
import com.freshkeeper.model.ProductData
import com.freshkeeper.model.ProductDetails
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.math.BigDecimal
import java.math.RoundingMode
import java.net.SocketTimeoutException
import java.util.Locale
import javax.inject.Inject

class ProductDetailsServiceImpl
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : ProductDetailsService {
        private val firestore = FirebaseFirestore.getInstance()
        private val client = OkHttpClient()
        private val gson = Gson()
        private val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("FreshKeeperPrefs", MODE_PRIVATE)

        override suspend fun fetchProductDetails(barcode: String): ProductDetails? {
            return withContext(Dispatchers.IO) {
                val docRef = firestore.collection("productDetails").document(barcode)
                val snapshot = docRef.get().await()
                if (snapshot.exists()) {
                    return@withContext snapshot.toObject(ProductDetails::class.java)
                } else {
                    val languageCode =
                        sharedPreferences
                            .getString("language", Locale.getDefault().language) ?: "en"
                    val url = getOpenFoodFactsUrl(languageCode, barcode)
                    val request = Request.Builder().url(url).build()
                    try {
                        client.newCall(request).execute().use { response ->
                            if (!response.isSuccessful) {
                                Log.e(
                                    "ProductDetailsService",
                                    "API response unsuccessful for barcode: $barcode. " +
                                        "Code: ${response.code}",
                                )
                                return@withContext null
                            }
                            val body = response.body.string()
                            val product = gson.fromJson(body, ApiResponse::class.java).product

                            val nutriments =
                                product.nutriments?.let {
                                    Nutriments(
                                        energyKcal =
                                            it.energyKcal?.let { value ->
                                                BigDecimal(value)
                                                    .setScale(2, RoundingMode.DOWN)
                                                    .toDouble()
                                            },
                                        fat =
                                            it.fat?.let { value ->
                                                BigDecimal(value)
                                                    .setScale(2, RoundingMode.DOWN)
                                                    .toDouble()
                                            },
                                        carbohydrates =
                                            it.carbohydrates?.let { value ->
                                                BigDecimal(value)
                                                    .setScale(2, RoundingMode.DOWN)
                                                    .toDouble()
                                            },
                                        sugars =
                                            it.sugars?.let { value ->
                                                BigDecimal(value)
                                                    .setScale(2, RoundingMode.DOWN)
                                                    .toDouble()
                                            },
                                        fiber =
                                            it.fiber?.let { value ->
                                                BigDecimal(value)
                                                    .setScale(2, RoundingMode.DOWN)
                                                    .toDouble()
                                            },
                                        proteins =
                                            it.proteins?.let { value ->
                                                BigDecimal(value)
                                                    .setScale(2, RoundingMode.DOWN)
                                                    .toDouble()
                                            },
                                        salt =
                                            it.salt?.let { value ->
                                                BigDecimal(value)
                                                    .setScale(2, RoundingMode.DOWN)
                                                    .toDouble()
                                            },
                                    )
                                }
                            val productDetails =
                                ProductDetails(
                                    productName = product.productName,
                                    brand = product.brands,
                                    nutriScore = product.nutriscoreGrade,
                                    ingredients = product.ingredientsText,
                                    labels = product.labelsTags,
                                    vegan = product.labelsTags?.contains("en:vegan"),
                                    vegetarian = product.labelsTags?.contains("en:vegetarian"),
                                    organic = product.labelsTags?.contains("en:organic"),
                                    nutriments = nutriments,
                                )

                            docRef.set(productDetails).await()
                            return@withContext productDetails
                        }
                    } catch (e: Exception) {
                        Log.e(
                            "ProductDetailsService",
                            "Exception fetching product details for barcode: $barcode. " +
                                "Error: ${e.message}",
                        )
                        e.printStackTrace()
                        return@withContext null
                    }
                }
            }
        }

        override suspend fun fetchProductDataFromBarcode(
            barcode: String,
            onSuccess: (ProductData) -> Unit,
            onFailure: (Exception) -> Unit,
        ): ProductData? {
            return withContext(Dispatchers.IO) {
                val languageCode =
                    sharedPreferences
                        .getString("language", Locale.getDefault().language) ?: "en"
                val url = getOpenFoodFactsUrl(languageCode, barcode)
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

                        val productData = ProductData(name, quantity, unit, imageUrl)
                        onSuccess(productData)
                        return@withContext productData
                    }
                } catch (_: SocketTimeoutException) {
                    withContext(Dispatchers.Main) {
                        Toast
                            .makeText(
                                context,
                                "Network error: Timeout",
                                Toast.LENGTH_SHORT,
                            ).show()
                    }
                    onFailure(Exception("Network error: Timeout"))
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
                    onFailure(e)
                    return@withContext null
                }
            }
        }

        override suspend fun splitQuantityAndUnit(quantityWithUnit: String): Pair<String, String> {
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

        override suspend fun getOpenFoodFactsUrl(
            languageCode: String,
            barcode: String,
        ): String = "https://$languageCode.openfoodfacts.org/api/v3/product/$barcode.json"
    }

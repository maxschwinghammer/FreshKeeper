package com.freshkeeper.service

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.freshkeeper.model.Nutriments
import com.freshkeeper.model.ProductData
import com.freshkeeper.model.ProductDetails
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.math.BigDecimal
import java.math.RoundingMode
import java.net.SocketTimeoutException
import javax.inject.Inject

class ProductDetailsServiceImpl
    @Inject
    constructor() : ProductDetailsService {
        private val firestore = FirebaseFirestore.getInstance()
        private val client = OkHttpClient()
        private val gson = Gson()

        override suspend fun fetchProductDetails(barcode: String): ProductDetails? {
            return withContext(Dispatchers.IO) {
                val docRef = firestore.collection("productDetails").document(barcode)
                val snapshot = docRef.get().await()
                if (snapshot.exists()) {
                    return@withContext snapshot.toObject(ProductDetails::class.java)
                } else {
                    val url = "https://world.openfoodfacts.org/api/v3/product/$barcode.json"
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
                            val apiResponse = gson.fromJson(body, OpenFoodFactsResponse::class.java)

                            val product = apiResponse.product
                            val nutriments =
                                product.nutriments?.let {
                                    Nutriments(
                                        energyKcal =
                                            it.energyKcal?.let { value ->
                                                BigDecimal(value)
                                                    .setScale(
                                                        2,
                                                        RoundingMode.DOWN,
                                                    ).toDouble()
                                            },
                                        fat =
                                            it.fat?.let { value ->
                                                BigDecimal(value)
                                                    .setScale(
                                                        2,
                                                        RoundingMode.DOWN,
                                                    ).toDouble()
                                            },
                                        carbohydrates =
                                            it.carbohydrates?.let { value ->
                                                BigDecimal(value)
                                                    .setScale(
                                                        2,
                                                        RoundingMode.DOWN,
                                                    ).toDouble()
                                            },
                                        sugars =
                                            it.sugars?.let { value ->
                                                BigDecimal(value)
                                                    .setScale(
                                                        2,
                                                        RoundingMode.DOWN,
                                                    ).toDouble()
                                            },
                                        fiber =
                                            it.fiber?.let { value ->
                                                BigDecimal(value)
                                                    .setScale(
                                                        2,
                                                        RoundingMode.DOWN,
                                                    ).toDouble()
                                            },
                                        proteins =
                                            it.proteins?.let { value ->
                                                BigDecimal(value)
                                                    .setScale(
                                                        2,
                                                        RoundingMode.DOWN,
                                                    ).toDouble()
                                            },
                                        salt =
                                            it.salt?.let { value ->
                                                BigDecimal(value)
                                                    .setScale(
                                                        2,
                                                        RoundingMode.DOWN,
                                                    ).toDouble()
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

        override suspend fun fetchAndSaveProductDetails(barcode: String) {
            return withContext(Dispatchers.IO) {
                val docRef = firestore.collection("productDetails").document(barcode)
                if (docRef.get().await().exists()) return@withContext
                val url = "https://world.openfoodfacts.org/api/v3/product/$barcode.json"
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                if (!response.isSuccessful) return@withContext
                val body = response.body.string()

                val apiResponse = gson.fromJson(body, OpenFoodFactsResponse::class.java)

                val product = apiResponse.product
                val nutriments =
                    product.nutriments?.let { it ->
                        Nutriments(
                            energyKcal = it.energyKcal?.takeIf { it.isFinite() } ?: 0.0,
                            fat = it.fat?.takeIf { it.isFinite() } ?: 0.0,
                            carbohydrates = it.carbohydrates?.takeIf { it.isFinite() } ?: 0.0,
                            sugars = it.sugars?.takeIf { it.isFinite() } ?: 0.0,
                            fiber = it.fiber?.takeIf { it.isFinite() } ?: 0.0,
                            proteins = it.proteins?.takeIf { it.isFinite() } ?: 0.0,
                            salt = it.salt?.takeIf { it.isFinite() } ?: 0.0,
                        )
                    } ?: Nutriments(
                        energyKcal = null,
                        fat = null,
                        carbohydrates = null,
                        sugars = null,
                        fiber = null,
                        proteins = null,
                        salt = null,
                    )

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
            }
        }

        override suspend fun fetchProductDataFromBarcode(
            context: Context,
            barcode: String,
        ): ProductData? {
            return withContext(Dispatchers.IO) {
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
                        Toast
                            .makeText(
                                context,
                                "Network error: Timeout",
                                Toast.LENGTH_SHORT,
                            ).show()
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
    }

data class OpenFoodFactsResponse(
    val status: String,
    val product: Product,
)

data class Product(
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

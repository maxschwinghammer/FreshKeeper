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
import java.net.SocketTimeoutException
import javax.inject.Inject

class ProductDetailsServiceImpl
    @Inject
    constructor() : ProductDetailsService {
        private val firestore = FirebaseFirestore.getInstance()
        private val client = OkHttpClient()
        private val gson = Gson()

        override suspend fun fetchProductDetails(barcode: String): ProductDetails? {
            val docRef = firestore.collection("productDetails").document(barcode)
            val snapshot = docRef.get().await()
            if (snapshot.exists()) {
                Log.d(
                    "ProductDetailsService",
                    "Firestore document found for barcode: $barcode",
                )
                return snapshot.toObject(ProductDetails::class.java)
            } else {
                Log.d(
                    "ProductDetailsService",
                    "No Firestore document for barcode: $barcode. Fetching from API...",
                )
                val url = "https://world.openfoodfacts.org/api/v3/product/$barcode.json"
                val request = Request.Builder().url(url).build()
                try {
                    Log.d("API", "Try block starts executing")
                    client.newCall(request).execute().use { response ->
                        Log.d("API", response.toString())
                        if (!response.isSuccessful) {
                            Log.e(
                                "ProductDetailsService",
                                "API response unsuccessful for barcode: $barcode. " +
                                    "Code: ${response.code}",
                            )
                            return null
                        }
                        val body = response.body.string()
                        Log.d("Body", body)
                        val apiResponse = gson.fromJson(body, OpenFoodFactsResponse::class.java)
                        Log.d("Api", apiResponse.toString())
                        if (apiResponse.status != 1) {
                            Log.d(
                                "ProductDetailsService",
                                "API response status not OK for barcode: $barcode. " +
                                    "Status: ${apiResponse.status}",
                            )
                            return null
                        }
                        val product = apiResponse.product
                        val nutriments =
                            product.nutriments?.let {
                                Nutriments(
                                    energyKcal = it.energyKcal,
                                    fat = it.fat,
                                    carbohydrates = it.carbohydrates,
                                    sugars = it.sugars,
                                    fiber = it.fiber,
                                    proteins = it.proteins,
                                    salt = it.salt,
                                )
                            }
                        val productDetails =
                            ProductDetails(
                                productName = product.productName,
                                brand = product.brands,
                                nutriScore = product.nutriscoreGrade,
                                ingredients = product.ingredientsText,
                                labels = product.labelsTags,
                                isVegan = product.labelsTags?.contains("en:vegan"),
                                isVegetarian = product.labelsTags?.contains("en:vegetarian"),
                                isOrganic = product.labelsTags?.contains("en:organic"),
                                nutriments = nutriments,
                            )
                        docRef.set(productDetails).await()
                        Log.d(
                            "ProductDetailsService",
                            "Product details fetched and saved for barcode: $barcode",
                        )
                        return productDetails
                    }
                } catch (e: Exception) {
                    Log.e(
                        "ProductDetailsService",
                        "Exception fetching product details for barcode: $barcode. " +
                            "Error: ${e.message}",
                    )
                    return null
                }
            }
        }

        override suspend fun fetchAndSaveProductDetails(barcode: String) {
            val docRef = firestore.collection("productDetails").document(barcode)
            if (docRef.get().await().exists()) return
            val url = "https://world.openfoodfacts.org/api/v3/product/$barcode.json"
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return
            val body = response.body.string()
            val apiResponse = gson.fromJson(body, OpenFoodFactsResponse::class.java)
            Log.d("API", apiResponse.toString())
            if (apiResponse.status != 1) return
            val product = apiResponse.product
            val nutriments =
                product.nutriments?.let {
                    Nutriments(
                        energyKcal = it.energyKcal,
                        fat = it.fat,
                        carbohydrates = it.carbohydrates,
                        sugars = it.sugars,
                        fiber = it.fiber,
                        proteins = it.proteins,
                        salt = it.salt,
                    )
                }
            val productDetails =
                ProductDetails(
                    productName = product.productName,
                    brand = product.brands,
                    nutriScore = product.nutriscoreGrade,
                    ingredients = product.ingredientsText,
                    labels = product.labelsTags,
                    isVegan = product.labelsTags?.contains("en:vegan"),
                    isVegetarian = product.labelsTags?.contains("en:vegetarian"),
                    isOrganic = product.labelsTags?.contains("en:organic"),
                    nutriments = nutriments,
                )
            docRef.set(productDetails).await()
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
    val status: Int,
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

package com.freshkeeper.service.productDetails

import com.freshkeeper.model.ProductData
import com.freshkeeper.model.ProductDetails

interface ProductDetailsService {
    suspend fun fetchProductDataFromBarcode(
        barcode: String,
        onSuccess: (ProductData) -> Unit,
        onFailure: (Exception) -> Unit,
    ): ProductData?

    suspend fun splitQuantityAndUnit(quantityWithUnit: String): Pair<String, String>

    suspend fun fetchProductDetails(barcode: String): ProductDetails?

    suspend fun fetchAndSaveProductDetails(barcode: String)

    suspend fun getOpenFoodFactsUrl(
        languageCode: String,
        barcode: String,
    ): String
}

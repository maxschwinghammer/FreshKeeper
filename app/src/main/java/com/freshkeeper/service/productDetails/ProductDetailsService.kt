package com.freshkeeper.service.productDetails

import android.content.Context
import com.freshkeeper.model.ProductData
import com.freshkeeper.model.ProductDetails

interface ProductDetailsService {
    suspend fun fetchProductDataFromBarcode(
        context: Context,
        barcode: String,
    ): ProductData?

    suspend fun splitQuantityAndUnit(quantityWithUnit: String): Pair<String, String>

    suspend fun fetchProductDetails(barcode: String): ProductDetails?

    suspend fun fetchAndSaveProductDetails(barcode: String)
}

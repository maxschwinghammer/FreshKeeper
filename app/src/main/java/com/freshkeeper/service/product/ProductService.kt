package com.freshkeeper.service.product

import android.content.Context
import com.freshkeeper.model.FoodItem
import com.freshkeeper.model.FoodItemPicture
import kotlinx.coroutines.CoroutineScope

interface ProductService {
    suspend fun addProduct(
        productName: String,
        barcode: String,
        expiryTimestamp: Long,
        quantity: Int,
        unit: String,
        storageLocation: String,
        category: String,
        image: String?,
        imageUrl: String?,
        householdId: String,
        coroutineScope: CoroutineScope,
        onSuccess: (FoodItem) -> Unit,
        onFailure: (Exception) -> Unit,
        context: Context,
    )

    fun getFoodItemPicture(
        imageId: String,
        onSuccess: (FoodItemPicture) -> Unit,
        onFailure: (Exception) -> Unit,
    )

    fun updateProduct(
        foodItem: FoodItem,
        productName: String,
        quantity: Int,
        unit: String,
        storageLocation: String,
        category: String,
        expiryDate: Long,
        isConsumedChecked: Boolean,
        isThrownAwayChecked: Boolean,
        coroutineScope: CoroutineScope,
        onSuccess: (FoodItem) -> Unit,
        context: Context,
    )

    suspend fun logActivity(
        foodItem: FoodItem,
        productName: String,
        activityType: String,
        context: Context,
        oldName: String? = null,
        oldQuantity: Int? = null,
    )
}

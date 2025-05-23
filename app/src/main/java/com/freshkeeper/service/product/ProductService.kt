package com.freshkeeper.service.product

import android.content.Context
import com.freshkeeper.model.EventType
import com.freshkeeper.model.FoodItem
import com.freshkeeper.model.FoodItemPicture
import kotlinx.coroutines.CoroutineScope

interface ProductService {
    suspend fun addProduct(
        productName: String,
        barcode: String?,
        expiryTimestamp: Long,
        quantity: Int,
        unit: String,
        storageLocation: String,
        category: String,
        image: String?,
        imageUrl: String?,
        coroutineScope: CoroutineScope,
        context: Context,
        onSuccess: (FoodItem) -> Unit,
        onFailure: (Exception) -> Unit,
    )

    suspend fun appendToCsv(
        productName: String,
        category: String,
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
        expiryTimestamp: Long,
        isConsumedChecked: Boolean,
        isThrownAwayChecked: Boolean,
        coroutineScope: CoroutineScope,
        onSuccess: (FoodItem) -> Unit,
    )

    suspend fun logActivity(
        foodItem: FoodItem,
        productName: String,
        activityType: EventType,
        oldName: String? = null,
        oldQuantity: Int? = null,
    )
}

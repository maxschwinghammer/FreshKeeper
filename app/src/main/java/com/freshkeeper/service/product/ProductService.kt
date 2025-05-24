package com.freshkeeper.service.product

import android.content.Context
import com.freshkeeper.model.Category
import com.freshkeeper.model.EventType
import com.freshkeeper.model.FoodItem
import com.freshkeeper.model.Picture
import com.freshkeeper.model.StorageLocation
import kotlinx.coroutines.CoroutineScope

interface ProductService {
    suspend fun addProduct(
        productName: String,
        barcode: String?,
        expiryTimestamp: Long,
        quantity: Int,
        unit: String,
        storageLocation: StorageLocation,
        category: Category,
        image: String?,
        imageUrl: String?,
        coroutineScope: CoroutineScope,
        context: Context,
        onSuccess: (FoodItem) -> Unit,
        onFailure: (Exception) -> Unit,
    )

    suspend fun appendToCsv(
        productName: String,
        category: Category,
        context: Context,
    )

    fun getFoodItemPicture(
        itemId: String,
        onSuccess: (Picture) -> Unit,
        onFailure: (Exception) -> Unit,
    )

    fun updateProduct(
        foodItem: FoodItem,
        productName: String,
        quantity: Int,
        unit: String,
        storageLocation: StorageLocation,
        category: Category,
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

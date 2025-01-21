package com.freshkeeper.model.service

import com.freshkeeper.model.FoodItem
import kotlinx.coroutines.CoroutineScope

interface ProductService {
    suspend fun addProduct(
        productName: String,
        expiryTimestamp: Long,
        quantity: Int,
        unit: String,
        storageLocation: String,
        category: String,
        imageUrl: String,
        householdId: String,
        coroutineScope: CoroutineScope,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit,
        addedText: String,
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
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit,
        addedText: String,
    )

    suspend fun logActivity(
        foodItem: FoodItem,
        productName: String,
        activityType: String,
        addedText: String,
    )
}

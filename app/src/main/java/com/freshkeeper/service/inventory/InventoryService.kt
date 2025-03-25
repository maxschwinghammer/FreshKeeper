package com.freshkeeper.service.inventory

import androidx.lifecycle.MutableLiveData
import com.freshkeeper.model.FoodItem

interface InventoryService {
    suspend fun getAllFoodItems(
        onResult: (List<FoodItem>) -> Unit,
        onFailure: (Exception) -> Unit,
    )

    suspend fun getStorageLocationItems(
        storageLocation: String,
        foodItemList: MutableLiveData<List<FoodItem>>,
        onResult: (List<FoodItem>) -> Unit,
        onFailure: (Exception) -> Unit,
    )
}

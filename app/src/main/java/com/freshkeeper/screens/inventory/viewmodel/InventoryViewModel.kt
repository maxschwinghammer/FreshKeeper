package com.freshkeeper.screens.inventory.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.freshkeeper.model.FoodItem
import com.freshkeeper.model.FoodItemPicture
import com.freshkeeper.screens.AppViewModel
import com.freshkeeper.service.inventory.InventoryService
import com.freshkeeper.service.product.ProductService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel
    @Inject
    constructor(
        private val inventoryService: InventoryService,
        private val productService: ProductService,
    ) : AppViewModel() {
        private val _foodItems = MutableLiveData<List<FoodItem>>()
        val foodItems: LiveData<List<FoodItem>> = _foodItems

        private val _itemList = MutableLiveData<String>()
        val itemList: LiveData<String> = _itemList

        private val _fridgeItems = MutableLiveData<List<FoodItem>>(emptyList())
        val fridgeItems: LiveData<List<FoodItem>> = _fridgeItems

        private val _cupboardItems = MutableLiveData<List<FoodItem>>(emptyList())
        val cupboardItems: LiveData<List<FoodItem>> = _cupboardItems

        private val _freezerItems = MutableLiveData<List<FoodItem>>(emptyList())
        val freezerItems: LiveData<List<FoodItem>> = _freezerItems

        private val _countertopItems = MutableLiveData<List<FoodItem>>(emptyList())
        val countertopItems: LiveData<List<FoodItem>> = _countertopItems

        private val _cellarItems = MutableLiveData<List<FoodItem>>(emptyList())
        val cellarItems: LiveData<List<FoodItem>> = _cellarItems

        private val _bakeryItems = MutableLiveData<List<FoodItem>>(emptyList())
        val bakeryItems: LiveData<List<FoodItem>> = _bakeryItems

        private val _spiceRackItems = MutableLiveData<List<FoodItem>>(emptyList())
        val spiceRackItems: LiveData<List<FoodItem>> = _spiceRackItems

        private val _pantryItems = MutableLiveData<List<FoodItem>>(emptyList())
        val pantryItems: LiveData<List<FoodItem>> = _pantryItems

        private val _fruitBasketItems = MutableLiveData<List<FoodItem>>(emptyList())
        val fruitBasketItems: LiveData<List<FoodItem>> = _fruitBasketItems

        private val _otherItems = MutableLiveData<List<FoodItem>>(emptyList())
        val otherItems: LiveData<List<FoodItem>> = _otherItems

        init {
            getStorageLocationItems("fridge", _fridgeItems)
            getStorageLocationItems("cupboard", _cupboardItems)
            getStorageLocationItems("freezer", _freezerItems)
            getStorageLocationItems("counter_top", _countertopItems)
            getStorageLocationItems("cellar", _cellarItems)
            getStorageLocationItems("bread_box", _bakeryItems)
            getStorageLocationItems("spice_rack", _spiceRackItems)
            getStorageLocationItems("pantry", _pantryItems)
            getStorageLocationItems("fruit_basket", _fruitBasketItems)
            getStorageLocationItems("other", _otherItems)
            getAllFoodItems()
        }

        private fun getAllFoodItems() {
            launchCatching {
                inventoryService.getAllFoodItems(
                    onResult = { items ->
                        _foodItems.value = items
                        updateItemList(items)
                    },
                    onFailure = {
                        Log.e("InventoryViewModel", "Error loading all food items")
                    },
                )
            }
        }

        private fun updateItemList(foodItems: List<FoodItem>) {
            _itemList.value = foodItems.joinToString(separator = ", ") { it.name }
        }

        private fun getStorageLocationItems(
            storageLocation: String,
            foodItemList: MutableLiveData<List<FoodItem>>,
        ) {
            launchCatching {
                inventoryService.getStorageLocationItems(
                    storageLocation,
                    foodItemList,
                    onResult = { items ->
                        when (storageLocation) {
                            "fridge" -> _fridgeItems.value = items
                            "cupboard" -> _cupboardItems.value = items
                            "freezer" -> _freezerItems.value = items
                            "countertop" -> _countertopItems.value = items
                            "cellar" -> _cellarItems.value = items
                            "bakery" -> _bakeryItems.value = items
                            "spiceRack" -> _spiceRackItems.value = items
                            "pantry" -> _pantryItems.value = items
                            "fruitBasket" -> _fruitBasketItems.value = items
                            "other" -> _otherItems.value = items
                        }
                    },
                    onFailure = {
                        Log.e(
                            "InventoryViewModel",
                            "Error loading storage location items",
                        )
                    },
                )
            }
        }

        fun addProduct(
            productName: String,
            barcode: String,
            expiryDate: Long,
            quantity: Int,
            unit: String,
            storageLocation: String,
            category: String,
            image: String?,
            imageUrl: String,
            householdId: String,
            coroutineScope: CoroutineScope,
            onSuccess: () -> Unit,
            context: Context,
        ) {
            launchCatching {
                productService.addProduct(
                    productName,
                    barcode,
                    expiryDate,
                    quantity,
                    unit,
                    storageLocation,
                    category,
                    image,
                    imageUrl,
                    householdId,
                    coroutineScope,
                    onSuccess = { newItem ->
                        _foodItems.value = (_foodItems.value ?: emptyList()) + newItem
                        when (newItem.storageLocation) {
                            "fridge" -> _fridgeItems.value = (_fridgeItems.value ?: emptyList()) + newItem
                            "cupboard" -> _cupboardItems.value = (_cupboardItems.value ?: emptyList()) + newItem
                            "freezer" -> _freezerItems.value = (_freezerItems.value ?: emptyList()) + newItem
                            "counter_top", "countertop" -> _countertopItems.value = (_countertopItems.value ?: emptyList()) + newItem
                            "cellar" -> _cellarItems.value = (_cellarItems.value ?: emptyList()) + newItem
                            "bread_box", "bakery" -> _bakeryItems.value = (_bakeryItems.value ?: emptyList()) + newItem
                            "spice_rack", "spiceRack" -> _spiceRackItems.value = (_spiceRackItems.value ?: emptyList()) + newItem
                            "pantry" -> _pantryItems.value = (_pantryItems.value ?: emptyList()) + newItem
                            "fruit_basket", "fruitBasket" -> _fruitBasketItems.value = (_fruitBasketItems.value ?: emptyList()) + newItem
                            "other" -> _otherItems.value = (_otherItems.value ?: emptyList()) + newItem
                        }
                        onSuccess()
                    },
                    { e ->
                        Log.e("ProductService", "Error adding product", e)
                    },
                    context,
                )
            }
        }

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
            context: Context,
        ) {
            launchCatching {
                productService.updateProduct(
                    foodItem,
                    productName,
                    quantity,
                    unit,
                    storageLocation,
                    category,
                    expiryDate,
                    isConsumedChecked,
                    isThrownAwayChecked,
                    coroutineScope,
                    onSuccess = { updatedItem ->
                        _foodItems.value =
                            _foodItems.value?.map { currentItem ->
                                if (currentItem.id == updatedItem.id) updatedItem else currentItem
                            }

                        updateStorageLists(updatedItem)
                        onSuccess()
                    },
                    context,
                )
            }
        }

        private fun updateStorageLists(updatedItem: FoodItem) {
            val storageLists =
                listOf(
                    _fridgeItems,
                    _cupboardItems,
                    _freezerItems,
                    _countertopItems,
                    _cellarItems,
                    _bakeryItems,
                    _spiceRackItems,
                    _pantryItems,
                    _fruitBasketItems,
                    _otherItems,
                )

            storageLists.forEach { liveDataList ->
                liveDataList.value =
                    liveDataList.value?.map { currentItem ->
                        if (currentItem.id == updatedItem.id) updatedItem else currentItem
                    }
            }
        }

        fun getFoodItemPicture(
            imageId: String,
            onSuccess: (FoodItemPicture) -> Unit,
            onFailure: (Exception) -> Unit,
        ) {
            launchCatching {
                productService.getFoodItemPicture(imageId, onSuccess, onFailure)
            }
        }
    }

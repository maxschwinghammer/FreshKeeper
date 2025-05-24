package com.freshkeeper.screens.inventory.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.freshkeeper.R
import com.freshkeeper.model.Category
import com.freshkeeper.model.FoodItem
import com.freshkeeper.model.FoodStatus
import com.freshkeeper.model.ProductData
import com.freshkeeper.model.StorageLocation
import com.freshkeeper.screens.AppViewModel
import com.freshkeeper.service.inventory.InventoryService
import com.freshkeeper.service.product.ProductService
import com.freshkeeper.service.productDetails.ProductDetailsService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import kotlin.collections.joinToString

@HiltViewModel
class InventoryViewModel
    @Inject
    constructor(
        private val inventoryService: InventoryService,
        private val productService: ProductService,
        private val productDetailsService: ProductDetailsService,
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

        private val _counterTopItems = MutableLiveData<List<FoodItem>>(emptyList())
        val counterTopItems: LiveData<List<FoodItem>> = _counterTopItems

        private val _cellarItems = MutableLiveData<List<FoodItem>>(emptyList())
        val cellarItems: LiveData<List<FoodItem>> = _cellarItems

        private val _breadBoxItems = MutableLiveData<List<FoodItem>>(emptyList())
        val breadBoxItems: LiveData<List<FoodItem>> = _breadBoxItems

        private val _spiceRackItems = MutableLiveData<List<FoodItem>>(emptyList())
        val spiceRackItems: LiveData<List<FoodItem>> = _spiceRackItems

        private val _pantryItems = MutableLiveData<List<FoodItem>>(emptyList())
        val pantryItems: LiveData<List<FoodItem>> = _pantryItems

        private val _fruitBasketItems = MutableLiveData<List<FoodItem>>(emptyList())
        val fruitBasketItems: LiveData<List<FoodItem>> = _fruitBasketItems

        private val _otherItems = MutableLiveData<List<FoodItem>>(emptyList())
        val otherItems: LiveData<List<FoodItem>> = _otherItems

        init {
            getStorageLocationItems(StorageLocation.FRIDGE, _fridgeItems)
            getStorageLocationItems(StorageLocation.CUPBOARD, _cupboardItems)
            getStorageLocationItems(StorageLocation.FREEZER, _freezerItems)
            getStorageLocationItems(StorageLocation.COUNTER_TOP, _counterTopItems)
            getStorageLocationItems(StorageLocation.CELLAR, _cellarItems)
            getStorageLocationItems(StorageLocation.BREAD_BOX, _breadBoxItems)
            getStorageLocationItems(StorageLocation.SPICE_RACK, _spiceRackItems)
            getStorageLocationItems(StorageLocation.PANTRY, _pantryItems)
            getStorageLocationItems(StorageLocation.FRUIT_BASKET, _fruitBasketItems)
            getStorageLocationItems(StorageLocation.OTHER, _otherItems)
        }

        fun getAllFoodItems(context: Context) {
            launchCatching {
                inventoryService.getAllFoodItems(
                    onResult = { items ->
                        _foodItems.value = items
                        _itemList.value =
                            items.joinToString(separator = ", ") {
                                val expiryText =
                                    if (it.daysDifference < 0) {
                                        context.getString(
                                            R.string.expired_text,
                                            -it.daysDifference,
                                        )
                                    } else {
                                        context.getString(
                                            R.string.expires_in_text,
                                            it.daysDifference,
                                        )
                                    }
                                "${it.name} (${it.quantity} ${it.unit}, $expiryText)"
                            }
                    },
                    onFailure = {
                        Log.e("InventoryViewModel", "Error loading all food items")
                    },
                )
            }
        }

        private fun getStorageLocationItems(
            storageLocation: StorageLocation,
            foodItemList: MutableLiveData<List<FoodItem>>,
        ) {
            launchCatching {
                inventoryService.getStorageLocationItems(
                    storageLocation,
                    foodItemList,
                    onResult = { items ->
                        when (storageLocation) {
                            StorageLocation.FRIDGE -> _fridgeItems.value = items
                            StorageLocation.CUPBOARD -> _cupboardItems.value = items
                            StorageLocation.FREEZER -> _freezerItems.value = items
                            StorageLocation.COUNTER_TOP -> _counterTopItems.value = items
                            StorageLocation.CELLAR -> _cellarItems.value = items
                            StorageLocation.BREAD_BOX -> _breadBoxItems.value = items
                            StorageLocation.SPICE_RACK -> _spiceRackItems.value = items
                            StorageLocation.PANTRY -> _pantryItems.value = items
                            StorageLocation.FRUIT_BASKET -> _fruitBasketItems.value = items
                            StorageLocation.OTHER -> _otherItems.value = items
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

        suspend fun fetchProductDataFromBarcode(
            barcode: String,
            onSuccess: (ProductData) -> Unit,
            onFailure: (Exception) -> Unit,
        ): ProductData? =
            productDetailsService.fetchProductDataFromBarcode(
                barcode,
                onSuccess,
                onFailure,
            )

        fun addProduct(
            productName: String,
            barcode: String?,
            expiryTimestamp: Long,
            quantity: Int,
            unit: String,
            storageLocation: StorageLocation,
            category: Category,
            image: String?,
            imageUrl: String,
            coroutineScope: CoroutineScope,
            context: Context,
            onSuccess: () -> Unit,
        ) {
            launchCatching {
                productService.addProduct(
                    productName,
                    barcode,
                    expiryTimestamp,
                    quantity,
                    unit,
                    storageLocation,
                    category,
                    image,
                    imageUrl,
                    coroutineScope,
                    context,
                    onSuccess = { newItem ->
                        _foodItems.value = (_foodItems.value ?: emptyList()) + newItem
                        when (newItem.storageLocation) {
                            StorageLocation.FRIDGE ->
                                _fridgeItems.value = (_fridgeItems.value ?: emptyList()) + newItem
                            StorageLocation.CUPBOARD ->
                                _cupboardItems.value = (_cupboardItems.value ?: emptyList()) + newItem
                            StorageLocation.FREEZER ->
                                _freezerItems.value = (_freezerItems.value ?: emptyList()) + newItem
                            StorageLocation.COUNTER_TOP ->
                                _counterTopItems.value = (_counterTopItems.value ?: emptyList()) + newItem
                            StorageLocation.CELLAR ->
                                _cellarItems.value = (_cellarItems.value ?: emptyList()) + newItem
                            StorageLocation.BREAD_BOX ->
                                _breadBoxItems.value = (_breadBoxItems.value ?: emptyList()) + newItem
                            StorageLocation.SPICE_RACK ->
                                _spiceRackItems.value = (_spiceRackItems.value ?: emptyList()) + newItem
                            StorageLocation.PANTRY ->
                                _pantryItems.value = (_pantryItems.value ?: emptyList()) + newItem
                            StorageLocation.FRUIT_BASKET ->
                                _fruitBasketItems.value = (_fruitBasketItems.value ?: emptyList()) + newItem
                            StorageLocation.OTHER ->
                                _otherItems.value = (_otherItems.value ?: emptyList()) + newItem
                        }
                        onSuccess()
                    },
                    { e ->
                        Log.e("ProductService", "Error adding product", e)
                    },
                )
            }
        }

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
            onSuccess: () -> Unit,
        ) {
            launchCatching {
                productService.updateProduct(
                    foodItem,
                    productName,
                    quantity,
                    unit,
                    storageLocation,
                    category,
                    expiryTimestamp,
                    isConsumedChecked,
                    isThrownAwayChecked,
                    coroutineScope,
                    onSuccess = { updatedItem ->
                        val oldLoc = foodItem.storageLocation
                        val newLoc = updatedItem.storageLocation

                        if (oldLoc != newLoc) {
                            getListByLocation(oldLoc)?.value =
                                getListByLocation(oldLoc)
                                    ?.value
                                    ?.filterNot { it.id == updatedItem.id }
                            getListByLocation(newLoc)?.value =
                                (getListByLocation(newLoc)?.value ?: emptyList()) + updatedItem
                        } else {
                            updateStorageLists(updatedItem)
                        }

                        if (updatedItem.status == FoodStatus.CONSUMED ||
                            updatedItem.status == FoodStatus.THROWN_AWAY
                        ) {
                            _foodItems.value =
                                _foodItems.value?.filterNot {
                                    it.id ==
                                        updatedItem.id
                                }
                            removeFromStorageLists(updatedItem)
                        } else {
                            _foodItems.value =
                                _foodItems.value?.map { currentItem ->
                                    if (currentItem.id == updatedItem.id) {
                                        updatedItem
                                    } else {
                                        currentItem
                                    }
                                }
                            updateStorageLists(updatedItem)
                        }
                        onSuccess()
                    },
                )
            }
        }

        private fun getListByLocation(loc: StorageLocation): MutableLiveData<List<FoodItem>>? =
            when (loc) {
                StorageLocation.FRIDGE -> _fridgeItems
                StorageLocation.CUPBOARD -> _cupboardItems
                StorageLocation.FREEZER -> _freezerItems
                StorageLocation.COUNTER_TOP -> _counterTopItems
                StorageLocation.CELLAR -> _cellarItems
                StorageLocation.BREAD_BOX -> _breadBoxItems
                StorageLocation.SPICE_RACK -> _spiceRackItems
                StorageLocation.PANTRY -> _pantryItems
                StorageLocation.FRUIT_BASKET -> _fruitBasketItems
                StorageLocation.OTHER -> _otherItems
            }

        private fun removeFromStorageLists(deleted: FoodItem) {
            listOf(
                _fridgeItems,
                _cupboardItems,
                _freezerItems,
                _counterTopItems,
                _cellarItems,
                _breadBoxItems,
                _spiceRackItems,
                _pantryItems,
                _fruitBasketItems,
                _otherItems,
            ).forEach { liveDataList ->
                liveDataList.value = liveDataList.value?.filterNot { it.id == deleted.id }
            }
        }

        private fun updateStorageLists(updatedItem: FoodItem) {
            val storageLists =
                listOf(
                    _fridgeItems,
                    _cupboardItems,
                    _freezerItems,
                    _counterTopItems,
                    _cellarItems,
                    _breadBoxItems,
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
    }

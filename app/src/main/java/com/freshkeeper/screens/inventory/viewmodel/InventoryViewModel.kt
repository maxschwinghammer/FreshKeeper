package com.freshkeeper.screens.inventory.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.freshkeeper.model.FoodItem
import com.freshkeeper.screens.AppViewModel
import com.freshkeeper.service.inventory.InventoryService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel
    @Inject
    constructor(
        private val inventoryService: InventoryService,
    ) : AppViewModel() {
        private val _foodItems = MutableLiveData<List<FoodItem>>()
        val foodItems: LiveData<List<FoodItem>> = _foodItems

        private val _itemList = MutableLiveData<String>()
        val itemList: LiveData<String> = _itemList

        private val _fridgeItems = MutableLiveData<List<FoodItem>>()
        val fridgeItems: LiveData<List<FoodItem>> = _fridgeItems

        private val _cupboardItems = MutableLiveData<List<FoodItem>>()
        val cupboardItems: LiveData<List<FoodItem>> = _cupboardItems

        private val _freezerItems = MutableLiveData<List<FoodItem>>()
        val freezerItems: LiveData<List<FoodItem>> = _freezerItems

        private val _countertopItems = MutableLiveData<List<FoodItem>>()
        val countertopItems: LiveData<List<FoodItem>> = _countertopItems

        private val _cellarItems = MutableLiveData<List<FoodItem>>()
        val cellarItems: LiveData<List<FoodItem>> = _cellarItems

        private val _bakeryItems = MutableLiveData<List<FoodItem>>()
        val bakeryItems: LiveData<List<FoodItem>> = _bakeryItems

        private val _spiceRackItems = MutableLiveData<List<FoodItem>>()
        val spiceRackItems: LiveData<List<FoodItem>> = _spiceRackItems

        private val _pantryItems = MutableLiveData<List<FoodItem>>()
        val pantryItems: LiveData<List<FoodItem>> = _pantryItems

        private val _fruitBasketItems = MutableLiveData<List<FoodItem>>()
        val fruitBasketItems: LiveData<List<FoodItem>> = _fruitBasketItems

        private val _otherItems = MutableLiveData<List<FoodItem>>()
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
    }

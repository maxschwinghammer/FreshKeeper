package com.freshkeeper.screens.home.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.freshkeeper.model.FoodItem
import com.freshkeeper.model.FoodItemPicture
import com.freshkeeper.screens.AppViewModel
import com.freshkeeper.service.household.HouseholdService
import com.freshkeeper.service.membership.MembershipService
import com.freshkeeper.service.product.ProductService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val householdService: HouseholdService,
        private val membershipService: MembershipService,
        private val productService: ProductService,
    ) : AppViewModel() {
        private val _allFoodItems = MutableLiveData<List<FoodItem>>()
        val allFoodItems: LiveData<List<FoodItem>> = _allFoodItems

        private val _expiringSoonItems = MutableLiveData<List<FoodItem>>()
        val expiringSoonItems: LiveData<List<FoodItem>> = _expiringSoonItems

        private val _expiredItems = MutableLiveData<List<FoodItem>>()
        val expiredItems: LiveData<List<FoodItem>> = _expiredItems

        private val _isMember = MutableLiveData<Boolean>()
        val isMember: LiveData<Boolean> = _isMember

        init {
            launchCatching {
                getFoodItems()
                checkMembershipStatus()
            }
        }

        private suspend fun checkMembershipStatus() {
            val status = membershipService.isMember()
            _isMember.value = status
        }

        private suspend fun getFoodItems() {
            try {
                val foodItems = householdService.getFoodItems()
                _allFoodItems.value = foodItems
                _expiringSoonItems.value =
                    foodItems
                        .filter { it.daysDifference in 1..30 }
                        .sortedBy { it.expiryTimestamp }
                _expiredItems.value =
                    foodItems
                        .filter { it.daysDifference < 1 }
                        .sortedByDescending { it.expiryTimestamp }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error loading food items from Firestore", e)
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
            addedText: String,
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
                    { newItem ->
                        _allFoodItems.value = (_allFoodItems.value ?: emptyList()) + newItem
                        onSuccess()
                    },
                    { e ->
                        Log.e("ProductService", "Error adding product", e)
                    },
                    addedText,
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
            addedText: String,
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
                        onSuccess()
                        updateItem(updatedItem)
                    },
                    addedText,
                )
            }
        }

        private fun updateItem(updatedItem: FoodItem) {
            _allFoodItems.value =
                _allFoodItems.value?.map {
                    if (it.id == updatedItem.id) updatedItem else it
                }

            _expiringSoonItems.value =
                _expiringSoonItems.value?.map {
                    if (it.id == updatedItem.id) updatedItem else it
                }

            _expiredItems.value =
                _expiredItems.value?.map {
                    if (it.id == updatedItem.id) updatedItem else it
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

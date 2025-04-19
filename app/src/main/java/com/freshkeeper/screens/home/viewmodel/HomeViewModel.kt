package com.freshkeeper.screens.home.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.freshkeeper.model.FoodItem
import com.freshkeeper.model.FoodItemPicture
import com.freshkeeper.model.ProductData
import com.freshkeeper.screens.AppViewModel
import com.freshkeeper.service.household.HouseholdService
import com.freshkeeper.service.product.ProductService
import com.freshkeeper.service.productDetails.ProductDetailsService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val householdService: HouseholdService,
//        private val membershipService: MembershipService,
        private val productService: ProductService,
        private val productDetailsService: ProductDetailsService,
    ) : AppViewModel() {
        private val _allFoodItems = MutableLiveData<List<FoodItem>>()
        val allFoodItems: LiveData<List<FoodItem>> = _allFoodItems

        private val _expiringSoonItems = MutableLiveData<List<FoodItem>>()
        val expiringSoonItems: LiveData<List<FoodItem>> = _expiringSoonItems

        private val _expiredItems = MutableLiveData<List<FoodItem>>()
        val expiredItems: LiveData<List<FoodItem>> = _expiredItems

//        private val _isMember = MutableLiveData<Boolean>()
//        val isMember: LiveData<Boolean> = _isMember

        private val _householdId = MutableLiveData<String>()
        val householdId: LiveData<String> = _householdId

        init {
            launchCatching {
                getFoodItems()
//                checkMembershipStatus()
                getHouseholdId()
            }
        }

//        private suspend fun checkMembershipStatus() {
//            val status = membershipService.isMember()
//            _isMember.value = status
//        }

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

        private fun getHouseholdId() {
            launchCatching {
                householdService.getHouseholdId(
                    onResult = { householdId ->
                        _householdId.value = householdId
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
            barcode: String,
            expiryTimestamp: Long,
            quantity: Int,
            unit: String,
            storageLocation: String,
            category: String,
            image: String?,
            imageUrl: String,
            householdId: String,
            coroutineScope: CoroutineScope,
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
                    householdId,
                    coroutineScope,
                    { newItem ->
                        _allFoodItems.value = (_allFoodItems.value ?: emptyList()) + newItem
                        if (newItem.daysDifference in 1..30) {
                            _expiringSoonItems.value = (_expiringSoonItems.value ?: emptyList()) + newItem
                        } else if (newItem.daysDifference < 1) {
                            _expiredItems.value = (_expiredItems.value ?: emptyList()) + newItem
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
            storageLocation: String,
            category: String,
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
                        updateItem(updatedItem)
                        onSuccess()
                    },
                )
            }
        }

        private fun updateItem(updatedItem: FoodItem) {
            if (updatedItem.consumed || updatedItem.thrownAway) {
                removeItem(updatedItem)
            } else {
                _allFoodItems.value =
                    _allFoodItems.value
                        ?.map { if (it.id == updatedItem.id) updatedItem else it }

                _expiringSoonItems.value =
                    _expiringSoonItems.value
                        ?.filterNot { it.id == updatedItem.id }
                _expiredItems.value =
                    _expiredItems.value
                        ?.filterNot { it.id == updatedItem.id }

                val daysDiff = updatedItem.daysDifference
                if (daysDiff in 1..30) {
                    _expiringSoonItems.value = (_expiringSoonItems.value ?: emptyList()) + updatedItem
                } else if (daysDiff < 1) {
                    _expiredItems.value = (_expiredItems.value ?: emptyList()) + updatedItem
                }
            }
        }

        private fun removeItem(deleted: FoodItem) {
            _allFoodItems.value = _allFoodItems.value?.filterNot { it.id == deleted.id }
            _expiringSoonItems.value = _expiringSoonItems.value?.filterNot { it.id == deleted.id }
            _expiredItems.value = _expiredItems.value?.filterNot { it.id == deleted.id }
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

package com.freshkeeper.screens.inventory.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.freshkeeper.model.FoodItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel
    @Inject
    constructor() : ViewModel() {
        private val firestore = FirebaseFirestore.getInstance()

        private val _foodItems = MutableLiveData<List<FoodItem>>()
        val foodItems: LiveData<List<FoodItem>> = _foodItems

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

        private val userId = FirebaseAuth.getInstance().currentUser?.uid
        private var householdId: String? = null

        init {
            loadHouseholdId()
            loadStorageLocationItems("fridge", _fridgeItems)
            loadStorageLocationItems("cupboard", _cupboardItems)
            loadStorageLocationItems("freezer", _freezerItems)
            loadStorageLocationItems("counter_top", _countertopItems)
            loadStorageLocationItems("cellar", _cellarItems)
            loadStorageLocationItems("bread_box", _bakeryItems)
            loadStorageLocationItems("spice_rack", _spiceRackItems)
            loadStorageLocationItems("pantry", _pantryItems)
            loadStorageLocationItems("fruit_basket", _fruitBasketItems)
            loadStorageLocationItems("other", _otherItems)
            loadAllFoodItems()
        }

        private fun loadAllFoodItems() {
            if (userId == null) return

            val query =
                if (householdId != null) {
                    firestore
                        .collection("foodItems")
                        .whereEqualTo("householdId", householdId)
                } else {
                    firestore
                        .collection("foodItems")
                        .whereEqualTo("userId", userId)
                }

            query
                .whereEqualTo("consumed", false)
                .whereEqualTo("thrownAway", false)
                .get()
                .addOnSuccessListener { documents ->
                    val items = documents.documents.mapNotNull { it.toObject<FoodItem>() }
                    _foodItems.value = items
                }.addOnFailureListener {
                    _foodItems.value = emptyList()
                    Log.e("InventoryViewModel", "Error loading all food items", it)
                }
        }

        private fun loadHouseholdId() {
            if (userId == null) return

            firestore
                .collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    householdId = document.getString("householdId")
                }.addOnFailureListener {
                    Log.e("HomeViewModel", "Error loading householdId from Firestore")
                }
        }

        private fun loadStorageLocationItems(
            storageLocation: String,
            liveData: MutableLiveData<List<FoodItem>>,
        ) {
            if (userId == null) return

            val query =
                if (householdId != null) {
                    firestore
                        .collection("foodItems")
                        .whereEqualTo("householdId", householdId)
                } else {
                    firestore
                        .collection("foodItems")
                        .whereEqualTo("userId", userId)
                }

            query
                .whereEqualTo("storageLocation", storageLocation)
                .whereEqualTo("consumed", false)
                .whereEqualTo("thrownAway", false)
                .get()
                .addOnSuccessListener { documents ->
                    val items = documents.documents.mapNotNull { it.toObject<FoodItem>() }
                    liveData.value = items
                }.addOnFailureListener {
                    liveData.value = emptyList()
                    Log.e(
                        "InventoryViewModel",
                        "Error loading category items for storage location: $storageLocation",
                        it,
                    )
                }
        }
    }

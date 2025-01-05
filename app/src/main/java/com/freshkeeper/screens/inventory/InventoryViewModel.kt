package com.freshkeeper.screens.inventory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.freshkeeper.screens.home.viewmodel.FoodItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class InventoryViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

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

    private val _spicesItems = MutableLiveData<List<FoodItem>>()
    val spicesItems: LiveData<List<FoodItem>> = _spicesItems

    private val _pantryItems = MutableLiveData<List<FoodItem>>()
    val pantryItems: LiveData<List<FoodItem>> = _pantryItems

    init {
        loadCategoryItems("fridge", _fridgeItems)
        loadCategoryItems("cupboard", _cupboardItems)
        loadCategoryItems("freezer", _freezerItems)
        loadCategoryItems("countertop", _countertopItems)
        loadCategoryItems("cellar", _cellarItems)
        loadCategoryItems("bakery", _bakeryItems)
        loadCategoryItems("spices", _spicesItems)
        loadCategoryItems("pantry", _pantryItems)
    }

    private fun loadCategoryItems(
        category: String,
        liveData: MutableLiveData<List<FoodItem>>,
    ) {
        firestore
            .collection("foodItems")
            .whereEqualTo("category", category)
            .get()
            .addOnSuccessListener { documents ->
                val items = documents.documents.mapNotNull { it.toObject<FoodItem>() }
                liveData.value = items
            }.addOnFailureListener {
                liveData.value = emptyList()
            }
    }
}

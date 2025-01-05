package com.freshkeeper.screens.inventory

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.freshkeeper.R
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

    private val _fruitBasketItems = MutableLiveData<List<FoodItem>>()
    val fruitBasketItems: LiveData<List<FoodItem>> = _fruitBasketItems

    private val _otherItems = MutableLiveData<List<FoodItem>>()
    val otherItems: LiveData<List<FoodItem>> = _otherItems

    init {
        loadCategoryItems(R.string.fridge, _fridgeItems)
        loadCategoryItems(R.string.cupboard, _cupboardItems)
        loadCategoryItems(R.string.freezer, _freezerItems)
        loadCategoryItems(R.string.counter_top, _countertopItems)
        loadCategoryItems(R.string.cellar, _cellarItems)
        loadCategoryItems(R.string.bread_box, _bakeryItems)
        loadCategoryItems(R.string.spices, _spicesItems)
        loadCategoryItems(R.string.pantry, _pantryItems)
        loadCategoryItems(R.string.fruit_basket, _fruitBasketItems)
        loadCategoryItems(R.string.other, _otherItems)
    }

    private fun loadCategoryItems(
        category: Int,
        liveData: MutableLiveData<List<FoodItem>>,
    ) {
        firestore
            .collection("foodItems")
            .whereEqualTo("category", category)
            .get()
            .addOnSuccessListener { documents ->
                val items = documents.documents.mapNotNull { it.toObject<FoodItem>() }
                liveData.value = items
                documents.documents.forEach { document ->
                    Log.d("InventoryViewModel", "Category: ${document.getLong("category")}")
                }
            }.addOnFailureListener {
                liveData.value = emptyList()
                Log.e("InventoryViewModel", "Error loading category items", it)
            }
    }
}

package com.freshkeeper.service.inventory

import androidx.lifecycle.MutableLiveData
import com.freshkeeper.model.FoodItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import javax.inject.Inject

class InventoryServiceImpl
    @Inject
    constructor() : InventoryService {
        private val firestore = FirebaseFirestore.getInstance()
        private val userId = FirebaseAuth.getInstance().currentUser?.uid
        private var householdId: String? = null

        override suspend fun getAllFoodItems(
            onResult: (List<FoodItem>) -> Unit,
            onFailure: (Exception) -> Unit,
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
                .whereEqualTo("consumed", false)
                .whereEqualTo("thrownAway", false)
                .get()
                .addOnSuccessListener { documents ->
                    val items = documents.documents.mapNotNull { it.toObject<FoodItem>() }
                    onResult(items)
                }.addOnFailureListener(onFailure)
        }

        override suspend fun getStorageLocationItems(
            storageLocation: String,
            foodItemList: MutableLiveData<List<FoodItem>>,
            onResult: (List<FoodItem>) -> Unit,
            onFailure: (Exception) -> Unit,
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
                    onResult(items)
                }.addOnFailureListener(onFailure)
        }
    }

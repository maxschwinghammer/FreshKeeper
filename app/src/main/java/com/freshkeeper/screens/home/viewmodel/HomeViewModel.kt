package com.freshkeeper.screens.home.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.freshkeeper.model.FoodItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.Instant
import java.time.temporal.ChronoUnit

class HomeViewModel : ViewModel() {
    private val _expiringSoonItems = MutableLiveData<List<FoodItem>>()
    val expiringSoonItems: LiveData<List<FoodItem>> = _expiringSoonItems

    private val _expiredItems = MutableLiveData<List<FoodItem>>()
    val expiredItems: LiveData<List<FoodItem>> = _expiredItems

    private val firestore = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private var householdId: String? = null

    init {
        loadHouseholdId()
    }

    private fun loadHouseholdId() {
        if (userId == null) return

        firestore
            .collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                householdId = document.getString("householdId")
                loadFoodItemsFromFirestore()
            }.addOnFailureListener {
                Log.d("HomeViewModel", "Error loading householdId from Firestore")
            }
    }

    private fun loadFoodItemsFromFirestore() {
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
                val currentTimestamp = Instant.now().toEpochMilli()
                val foodItems =
                    documents.mapNotNull { doc ->
                        doc.toObject(FoodItem::class.java).apply {
                            daysDifference =
                                ChronoUnit.DAYS
                                    .between(
                                        Instant.ofEpochMilli(currentTimestamp),
                                        Instant.ofEpochMilli(expiryTimestamp),
                                    ).toInt()
                        }
                    }

                _expiringSoonItems.value =
                    foodItems.filter { it.daysDifference in 0..30 }.sortedBy { it.expiryTimestamp }

                _expiredItems.value =
                    foodItems.filter { it.daysDifference < 0 }.sortedByDescending { it.expiryTimestamp }
            }.addOnFailureListener {
                Log.d("HomeViewModel", "Error loading food items from Firestore")
            }
    }
}

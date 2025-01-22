package com.freshkeeper.service

import android.util.Log
import com.freshkeeper.model.Activity
import com.freshkeeper.model.FoodItem
import com.freshkeeper.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProductServiceImpl
    @Inject
    constructor(
        private val accountService: AccountService,
    ) : ProductService {
        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val coroutineScope = CoroutineScope(Dispatchers.IO)

        private var _user = MutableStateFlow<User?>(null)
        val user: StateFlow<User?> get() = _user.asStateFlow()

        init {
            coroutineScope.launch {
                _user.value = accountService.getUserObject()
            }
        }

        override suspend fun addProduct(
            productName: String,
            expiryTimestamp: Long,
            quantity: Int,
            unit: String,
            storageLocation: String,
            category: String,
            imageUrl: String,
            householdId: String,
            coroutineScope: CoroutineScope,
            onSuccess: () -> Unit,
            onFailure: (Exception) -> Unit,
            addedText: String,
        ) {
            val currentUser = _user.value ?: return
            val foodItem =
                FoodItem(
                    userId = currentUser.id,
                    householdId = householdId,
                    name = productName,
                    expiryTimestamp = expiryTimestamp,
                    quantity = quantity,
                    unit = unit,
                    storageLocation = storageLocation,
                    category = category,
                    consumed = false,
                    thrownAway = false,
                    imageUrl = imageUrl,
                )

            try {
                firestore
                    .collection("foodItems")
                    .add(foodItem)
                    .addOnSuccessListener { documentReference ->
                        val updatedFoodItem = foodItem.copy(id = documentReference.id)
                        firestore
                            .collection("foodItems")
                            .document(documentReference.id)
                            .update("id", documentReference.id)
                            .addOnSuccessListener {
                                coroutineScope.launch {
                                    if (currentUser.householdId != null) {
                                        logActivity(
                                            updatedFoodItem,
                                            productName,
                                            "product_added",
                                            addedText,
                                        )
                                    }
                                    onSuccess()
                                }
                            }.addOnFailureListener { e ->
                                onFailure(e)
                            }
                    }.addOnFailureListener { e ->
                        onFailure(e)
                    }
            } catch (e: Exception) {
                onFailure(e)
            }
        }

        override fun updateProduct(
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
            onFailure: (Exception) -> Unit,
            addedText: String,
        ) {
            val currentUser = _user.value ?: return

            firestore
                .collection("foodItems")
                .whereEqualTo("id", foodItem.id)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val document = querySnapshot.documents.firstOrNull()
                    if (document != null) {
                        val updates =
                            mapOf(
                                "name" to productName,
                                "quantity" to quantity,
                                "unit" to unit,
                                "storageLocation" to storageLocation,
                                "category" to category,
                                "expiryTimestamp" to expiryDate,
                                "consumed" to isConsumedChecked,
                                "thrownAway" to isThrownAwayChecked,
                            )

                        document.reference
                            .update(updates)
                            .addOnSuccessListener {
                                coroutineScope.launch {
                                    coroutineScope.launch {
                                        val activityType =
                                            when {
                                                isConsumedChecked -> "consumed"
                                                isThrownAwayChecked -> "thrown_away"
                                                else -> "edit"
                                            }
                                        if (currentUser.householdId != null) {
                                            logActivity(
                                                foodItem,
                                                productName,
                                                activityType,
                                                addedText,
                                            )
                                        }
                                        onSuccess()
                                    }

                                    onSuccess()
                                }
                            }.addOnFailureListener { e ->
                                Log.e("Firestore", "Error when updating the product", e)
                                onFailure(e)
                            }
                    } else {
                        Log.e("Firestore", "No document found with the given ID")
                        onFailure(Exception("Document not found"))
                    }
                }.addOnFailureListener { e ->
                    Log.e("Firestore", "Error retrieving document", e)
                    onFailure(e)
                }
        }

        override suspend fun logActivity(
            foodItem: FoodItem,
            productName: String,
            activityType: String,
            addedText: String,
        ) {
            val currentUser = _user.value ?: return

            val activityText =
                when (activityType) {
                    "consumed" -> "${currentUser.displayName} marked $productName as consumed"
                    "thrown_away" -> "${currentUser.displayName} marked $productName as thrown away"
                    "edit" -> "${currentUser.displayName} edited the product $productName"
                    "product_added" -> "${currentUser.displayName} $addedText: $productName"
                    else -> "${currentUser.displayName} performed an activity on $productName"
                }

            val householdId = accountService.getHouseholdId()

            val activity =
                Activity(
                    id = null,
                    userId = currentUser.id,
                    householdId = householdId,
                    type = activityType,
                    text = activityText,
                    timestamp = System.currentTimeMillis(),
                )

            firestore
                .collection("activities")
                .add(activity)
                .addOnSuccessListener { documentReference ->
                    val updatedActivity = activity.copy(id = documentReference.id)
                    documentReference.update("id", updatedActivity.id)
                }.addOnFailureListener { e ->
                    Log.e("Firestore", "Error when adding the activity", e)
                }
        }
    }

package com.freshkeeper.model.service

import android.util.Log
import com.freshkeeper.model.Activity
import com.freshkeeper.model.FoodItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProductServiceImpl
    @Inject
    constructor(
        private val accountService: AccountService,
    ) : ProductService {
        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()

        override suspend fun addProduct(
            productName: String,
            expiryTimestamp: Long,
            quantity: Int,
            unit: String,
            storageLocation: String,
            category: String,
            imageUrl: String,
            userId: String,
            householdId: String,
            coroutineScope: CoroutineScope,
            onSuccess: () -> Unit,
            onFailure: (Exception) -> Unit,
        ) {
            val foodItem =
                FoodItem(
                    id = System.currentTimeMillis(),
                    userId = userId,
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
                    .addOnSuccessListener {
                        coroutineScope.launch { onSuccess() }
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
            userName: String?,
            coroutineScope: CoroutineScope,
            onSuccess: () -> Unit,
            onFailure: (Exception) -> Unit,
        ) {
            val userId = auth.currentUser?.uid ?: return

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
                                    logActivity(
                                        foodItem,
                                        productName,
                                        isConsumedChecked,
                                        isThrownAwayChecked,
                                        userName ?: "Unknown",
                                        userId,
                                    )
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
            isConsumedChecked: Boolean,
            isThrownAwayChecked: Boolean,
            userName: String,
            userId: String,
        ) {
            val activityType =
                when {
                    isConsumedChecked -> "remove"
                    isThrownAwayChecked -> "remove"
                    productName != foodItem.name -> "edit"
                    else -> "edit"
                }

            val activityText =
                when {
                    isConsumedChecked -> "$userName marked $productName as consumed"
                    isThrownAwayChecked -> "$userName marked $productName as thrown away"
                    productName != foodItem.name -> "$userName edited the name of '${foodItem.name}' to '$productName'"
                    else -> "$userName edited the product $productName"
                }

            val householdId = accountService.getHouseholdId()

            val activity =
                Activity(
                    id = null,
                    userId = userId,
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

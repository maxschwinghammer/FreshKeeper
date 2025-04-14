package com.freshkeeper.service.product

import android.util.Log
import com.freshkeeper.model.Activity
import com.freshkeeper.model.FoodItem
import com.freshkeeper.model.FoodItemPicture
import com.freshkeeper.model.Household
import com.freshkeeper.model.User
import com.freshkeeper.service.account.AccountService
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
            barcode: String,
            expiryTimestamp: Long,
            quantity: Int,
            unit: String,
            storageLocation: String,
            category: String,
            image: String?,
            imageUrl: String?,
            householdId: String,
            coroutineScope: CoroutineScope,
            onSuccess: (FoodItem) -> Unit,
            onFailure: (Exception) -> Unit,
            addedText: String,
        ) {
            val currentUser = _user.value ?: return
            if (!image.isNullOrEmpty()) {
                val foodItemPicture = FoodItemPicture(image = image, type = "base64")
                firestore
                    .collection("foodItemPictures")
                    .add(foodItemPicture)
                    .addOnSuccessListener { pictureRef ->
                        val pictureId = pictureRef.id
                        val foodItem =
                            FoodItem(
                                barcode = barcode,
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
                                imageId = pictureId,
                            )
                        firestore
                            .collection("foodItems")
                            .add(foodItem)
                            .addOnSuccessListener { documentReference ->
                                firestore
                                    .collection("foodItems")
                                    .document(documentReference.id)
                                    .update("id", documentReference.id)
                                    .addOnSuccessListener {
                                        coroutineScope.launch {
                                            if (currentUser.householdId != null) {
                                                logActivity(
                                                    foodItem.copy(id = documentReference.id),
                                                    productName,
                                                    "product_added",
                                                    addedText,
                                                )
                                            }
                                            onSuccess(foodItem.copy(id = documentReference.id))
                                        }
                                    }.addOnFailureListener { e -> onFailure(e) }
                            }.addOnFailureListener { e -> onFailure(e) }
                    }.addOnFailureListener { e -> onFailure(e) }
            } else if (!imageUrl.isNullOrEmpty()) {
                val foodItemPicture = FoodItemPicture(image = imageUrl, type = "url")
                firestore
                    .collection("foodItemPictures")
                    .add(foodItemPicture)
                    .addOnSuccessListener { pictureRef ->
                        val pictureId = pictureRef.id
                        val foodItem =
                            FoodItem(
                                barcode = barcode,
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
                                imageId = pictureId,
                            )
                        firestore
                            .collection("foodItems")
                            .add(foodItem)
                            .addOnSuccessListener { documentReference ->
                                firestore
                                    .collection("foodItems")
                                    .document(documentReference.id)
                                    .update("id", documentReference.id)
                                    .addOnSuccessListener {
                                        coroutineScope.launch {
                                            if (currentUser.householdId != null) {
                                                logActivity(
                                                    foodItem.copy(id = documentReference.id),
                                                    productName,
                                                    "product_added",
                                                    addedText,
                                                )
                                            }
                                            onSuccess(foodItem.copy(id = documentReference.id))
                                        }
                                    }.addOnFailureListener { e -> onFailure(e) }
                            }.addOnFailureListener { e -> onFailure(e) }
                    }.addOnFailureListener { e -> onFailure(e) }
            } else {
                val foodItem =
                    FoodItem(
                        barcode = barcode,
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
                    )
                firestore
                    .collection("foodItems")
                    .add(foodItem)
                    .addOnSuccessListener { documentReference ->
                        firestore
                            .collection("foodItems")
                            .document(documentReference.id)
                            .update("id", documentReference.id)
                            .addOnSuccessListener {
                                coroutineScope.launch {
                                    if (currentUser.householdId != null) {
                                        logActivity(
                                            foodItem.copy(id = documentReference.id),
                                            productName,
                                            "product_added",
                                            addedText,
                                        )
                                    }
                                    onSuccess(foodItem.copy(id = documentReference.id))
                                }
                            }.addOnFailureListener { e -> onFailure(e) }
                    }.addOnFailureListener { e -> onFailure(e) }
            }
        }

        override fun getFoodItemPicture(
            imageId: String,
            onSuccess: (FoodItemPicture) -> Unit,
            onFailure: (Exception) -> Unit,
        ) {
            firestore
                .collection("foodItemPictures")
                .document(imageId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val foodItemPicture = documentSnapshot.toObject(FoodItemPicture::class.java)
                        if (foodItemPicture != null) {
                            onSuccess(foodItemPicture)
                        } else {
                            onFailure(Exception("FoodItemPicture not found"))
                        }
                    } else {
                        onFailure(Exception("No document found with the given imageId"))
                    }
                }.addOnFailureListener { e ->
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
            onSuccess: (FoodItem) -> Unit,
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
                                if (currentUser.householdId != null) {
                                    firestore
                                        .collection("households")
                                        .document(currentUser.householdId)
                                        .get()
                                        .addOnSuccessListener { householdDoc ->
                                            val household = householdDoc.toObject(Household::class.java)
                                            if (household != null && household.type != "Single household") {
                                                coroutineScope.launch {
                                                    val activityType =
                                                        when {
                                                            isConsumedChecked -> "consumed"
                                                            isThrownAwayChecked -> "thrown_away"
                                                            else -> "edit"
                                                        }
                                                    logActivity(
                                                        foodItem,
                                                        productName,
                                                        activityType,
                                                        addedText,
                                                    )
                                                    onSuccess(foodItem)
                                                }
                                            }
                                        }.addOnFailureListener { e ->
                                            Log.e("Firestore", "Error retrieving household", e)
                                        }
                                }
                            }.addOnFailureListener { e ->
                                Log.e("Firestore", "Error when updating the product", e)
                            }
                    } else {
                        Log.e("Firestore", "No document found with the given ID")
                    }
                }.addOnFailureListener { e ->
                    Log.e("Firestore", "Error retrieving document", e)
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

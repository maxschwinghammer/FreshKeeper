package com.freshkeeper.service.product

import android.content.Context
import android.util.Log
import com.freshkeeper.model.Activity
import com.freshkeeper.model.FoodItem
import com.freshkeeper.model.FoodItemPicture
import com.freshkeeper.model.Household
import com.freshkeeper.model.User
import com.freshkeeper.service.account.AccountService
import com.freshkeeper.service.household.HouseholdService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class ProductServiceImpl
    @Inject
    constructor(
        private val accountService: AccountService,
        private val householdService: HouseholdService,
    ) : ProductService {
        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val coroutineScope = CoroutineScope(Dispatchers.IO)

        var user: User = User()
        var householdId: String? = null

        init {
            coroutineScope.launch {
                user = accountService.getUserObject()
                householdId = householdService.getHouseholdId()
            }
        }

        override suspend fun addProduct(
            productName: String,
            barcode: String?,
            expiryTimestamp: Long,
            quantity: Int,
            unit: String,
            storageLocation: String,
            category: String,
            image: String?,
            imageUrl: String?,
            coroutineScope: CoroutineScope,
            context: Context,
            onSuccess: (FoodItem) -> Unit,
            onFailure: (Exception) -> Unit,
        ) {
            val currentDate = Instant.ofEpochMilli(System.currentTimeMillis()).atZone(ZoneId.systemDefault()).toLocalDate()
            val expiryDate = Instant.ofEpochMilli(expiryTimestamp).atZone(ZoneId.systemDefault()).toLocalDate()
            val daysDifference = ChronoUnit.DAYS.between(currentDate, expiryDate).toInt()

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
                                userId = user.id,
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
                                            if (user.householdId != null) {
                                                logActivity(
                                                    foodItem.copy(id = documentReference.id),
                                                    productName,
                                                    "product_added",
                                                )
                                            }
                                            appendToCsv(productName, foodItem.category, context)
                                            onSuccess(
                                                foodItem.copy(
                                                    id = documentReference.id,
                                                    daysDifference = daysDifference,
                                                ),
                                            )
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
                                userId = user.id,
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
                                            if (user.householdId != null) {
                                                logActivity(
                                                    foodItem.copy(id = documentReference.id),
                                                    productName,
                                                    "product_added",
                                                )
                                            }
                                            appendToCsv(productName, foodItem.category, context)
                                            onSuccess(
                                                foodItem.copy(
                                                    id = documentReference.id,
                                                    daysDifference = daysDifference,
                                                ),
                                            )
                                        }
                                    }.addOnFailureListener { e -> onFailure(e) }
                            }.addOnFailureListener { e -> onFailure(e) }
                    }.addOnFailureListener { e -> onFailure(e) }
            } else {
                val foodItem =
                    FoodItem(
                        barcode = barcode,
                        userId = user.id,
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
                                    if (user.householdId != null) {
                                        logActivity(
                                            foodItem.copy(id = documentReference.id),
                                            productName,
                                            "product_added",
                                        )
                                    }
                                    appendToCsv(productName, foodItem.category, context)
                                    onSuccess(
                                        foodItem.copy(
                                            id = documentReference.id,
                                            daysDifference = daysDifference,
                                        ),
                                    )
                                }
                            }.addOnFailureListener { e -> onFailure(e) }
                    }.addOnFailureListener { e -> onFailure(e) }
            }
        }

        override suspend fun appendToCsv(
            productName: String,
            category: String,
            context: Context,
        ) {
            withContext(Dispatchers.IO) {
                val csvFile = File(context.filesDir, "name_category_mapping.csv")
                val line = "$productName,$category\n"
                csvFile.appendText(line)
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
            expiryTimestamp: Long,
            isConsumedChecked: Boolean,
            isThrownAwayChecked: Boolean,
            coroutineScope: CoroutineScope,
            onSuccess: (FoodItem) -> Unit,
        ) {
            firestore
                .collection("foodItems")
                .whereEqualTo("id", foodItem.id)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val document = querySnapshot.documents.firstOrNull()
                    if (document != null) {
                        val updates: MutableMap<String, Any> =
                            mutableMapOf(
                                "name" to productName,
                                "quantity" to quantity,
                                "unit" to unit,
                                "storageLocation" to storageLocation,
                                "category" to category,
                                "expiryTimestamp" to expiryTimestamp,
                                "consumed" to isConsumedChecked,
                                "thrownAway" to isThrownAwayChecked,
                            )

                        if ((isConsumedChecked || isThrownAwayChecked) &&
                            foodItem.discardTimestamp == null
                        ) {
                            updates["discardTimestamp"] = System.currentTimeMillis()
                        }

                        document.reference
                            .update(updates)
                            .addOnSuccessListener {
                                val currentDate =
                                    Instant
                                        .ofEpochMilli(
                                            System.currentTimeMillis(),
                                        ).atZone(ZoneId.systemDefault())
                                        .toLocalDate()
                                val expiryDate =
                                    Instant
                                        .ofEpochMilli(
                                            expiryTimestamp,
                                        ).atZone(ZoneId.systemDefault())
                                        .toLocalDate()
                                val updatedFoodItem =
                                    foodItem.copy(
                                        name = productName,
                                        quantity = quantity,
                                        unit = unit,
                                        storageLocation = storageLocation,
                                        category = category,
                                        expiryTimestamp = expiryTimestamp,
                                        consumed = isConsumedChecked,
                                        thrownAway = isThrownAwayChecked,
                                        daysDifference =
                                            ChronoUnit.DAYS
                                                .between(currentDate, expiryDate)
                                                .toInt(),
                                    )
                                onSuccess(updatedFoodItem)

                                if (user.householdId != null) {
                                    firestore
                                        .collection("households")
                                        .document(user.householdId!!)
                                        .get()
                                        .addOnSuccessListener { householdDoc ->
                                            val household =
                                                householdDoc.toObject(
                                                    Household::class.java,
                                                )
                                            if (household != null &&
                                                household.type != "Single household"
                                            ) {
                                                coroutineScope.launch {
                                                    val changedFields = mutableListOf<String>()
                                                    if (foodItem.name != productName) {
                                                        changedFields.add("name")
                                                    }
                                                    if (foodItem.quantity != quantity) {
                                                        changedFields.add("quantity")
                                                    }
                                                    if (foodItem.expiryTimestamp != expiryTimestamp) {
                                                        changedFields.add("expiry")
                                                    }
                                                    if (foodItem.storageLocation != storageLocation) {
                                                        changedFields.add("storage")
                                                    }
                                                    if (foodItem.category != category) {
                                                        changedFields.add("category")
                                                    }
                                                    val activityType =
                                                        when {
                                                            isConsumedChecked -> "consumed"
                                                            isThrownAwayChecked -> "thrown_away"
                                                            changedFields.size == 1 ->
                                                                changedFields.first()
                                                            else -> "edit"
                                                        }
                                                    logActivity(
                                                        updatedFoodItem,
                                                        productName,
                                                        activityType,
                                                        oldName = foodItem.name,
                                                        oldQuantity = foodItem.quantity,
                                                    )
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
            oldName: String?,
            oldQuantity: Int?,
        ) {
            val resolvedType =
                when (activityType) {
                    "quantity" -> {
                        if (oldQuantity != null && foodItem.quantity > oldQuantity) {
                            "quantity_increased"
                        } else {
                            "quantity_decreased"
                        }
                    }
                    else -> activityType
                }

            val activity =
                Activity(
                    userId = user.id,
                    householdId = householdId,
                    type = resolvedType,
                    userName = user.displayName!!,
                    timestamp = System.currentTimeMillis(),
                    oldProductName = oldName ?: "",
                    productName = productName,
                )

            firestore
                .collection("activities")
                .add(activity)
                .addOnSuccessListener { documentReference ->
                    documentReference.update("id", documentReference.id)
                }.addOnFailureListener { e ->
                    Log.e("Firestore", "Error when adding the activity", e)
                }
        }
    }

package com.freshkeeper.service.product

import android.content.Context
import android.util.Log
import com.freshkeeper.model.Activity
import com.freshkeeper.model.Category
import com.freshkeeper.model.EventType
import com.freshkeeper.model.FoodItem
import com.freshkeeper.model.FoodStatus
import com.freshkeeper.model.Household
import com.freshkeeper.model.HouseholdType
import com.freshkeeper.model.ImageType
import com.freshkeeper.model.Picture
import com.freshkeeper.model.StorageLocation
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
            storageLocation: StorageLocation,
            category: Category,
            image: String?,
            imageUrl: String?,
            coroutineScope: CoroutineScope,
            context: Context,
            onSuccess: (FoodItem) -> Unit,
            onFailure: (Exception) -> Unit,
        ) {
            val currentDate =
                Instant
                    .ofEpochMilli(System.currentTimeMillis())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
            val expiryDate =
                Instant
                    .ofEpochMilli(expiryTimestamp)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
            val daysDifference = ChronoUnit.DAYS.between(currentDate, expiryDate).toInt()

            val picture: Picture? =
                when {
                    !image.isNullOrEmpty() -> Picture(image = image, type = ImageType.BASE64)
                    !imageUrl.isNullOrEmpty() -> Picture(image = imageUrl, type = ImageType.URL)
                    else -> null
                }

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
                    status = FoodStatus.ACTIVE,
                    picture = picture,
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
                                        EventType.PRODUCT_ADDED,
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

        override suspend fun appendToCsv(
            productName: String,
            category: Category,
            context: Context,
        ) {
            withContext(Dispatchers.IO) {
                val csvFile = File(context.filesDir, "name_category_mapping.csv")
                val line = "$productName,$category\n"
                csvFile.appendText(line)
            }
        }

        override fun getFoodItemPicture(
            itemId: String,
            onSuccess: (Picture) -> Unit,
            onFailure: (Exception) -> Unit,
        ) {
            firestore
                .collection("foodItems")
                .document(itemId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val foodItemPicture = documentSnapshot.toObject(FoodItem::class.java)?.picture
                        if (foodItemPicture != null) {
                            onSuccess(foodItemPicture)
                        } else {
                            onFailure(Exception("No picture found in this FoodItem"))
                        }
                    } else {
                        onFailure(Exception("No FoodItem found"))
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
            storageLocation: StorageLocation,
            category: Category,
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
                                        status =
                                            if (isConsumedChecked) {
                                                FoodStatus.CONSUMED
                                            } else if (isThrownAwayChecked) {
                                                FoodStatus.THROWN_AWAY
                                            } else {
                                                FoodStatus.ACTIVE
                                            },
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
                                                household.type != HouseholdType.SINGLE
                                            ) {
                                                coroutineScope.launch {
                                                    val changedFields = mutableListOf<EventType>()
                                                    if (foodItem.name != productName) {
                                                        changedFields.add(EventType.NAME)
                                                    }
                                                    if (foodItem.quantity != quantity) {
                                                        changedFields.add(EventType.QUANTITY)
                                                    }
                                                    if (foodItem.expiryTimestamp != expiryTimestamp) {
                                                        changedFields.add(EventType.EXPIRY)
                                                    }
                                                    if (foodItem.storageLocation != storageLocation) {
                                                        changedFields.add(EventType.STORAGE)
                                                    }
                                                    if (foodItem.category != category) {
                                                        changedFields.add(EventType.CATEGORY)
                                                    }
                                                    val activityType =
                                                        when {
                                                            isConsumedChecked ->
                                                                EventType.CONSUMED
                                                            isThrownAwayChecked ->
                                                                EventType.THROWN_AWAY
                                                            changedFields.size == 1 ->
                                                                changedFields.first()
                                                            else -> EventType.EDIT
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
            activityType: EventType,
            oldName: String?,
            oldQuantity: Int?,
        ) {
            val resolvedType =
                when (activityType) {
                    EventType.QUANTITY -> {
                        if (oldQuantity != null && foodItem.quantity > oldQuantity) {
                            EventType.QUANTITY_INCREASED
                        } else {
                            EventType.QUANTITY_DECREASED
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
                    oldProductName =
                        if (
                            (resolvedType == EventType.NAME || resolvedType == EventType.EDIT) &&
                            !oldName.isNullOrBlank()
                        ) {
                            oldName
                        } else {
                            null
                        },
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

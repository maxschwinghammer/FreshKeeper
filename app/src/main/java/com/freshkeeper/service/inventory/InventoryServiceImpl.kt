package com.freshkeeper.service.inventory

import androidx.lifecycle.MutableLiveData
import com.freshkeeper.model.FoodItem
import com.freshkeeper.model.FoodStatus
import com.freshkeeper.model.StorageLocation
import com.freshkeeper.service.household.HouseholdService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class InventoryServiceImpl
    @Inject
    constructor(
        private val householdService: HouseholdService,
    ) : InventoryService {
        private val firestore = FirebaseFirestore.getInstance()
        private val userId = FirebaseAuth.getInstance().currentUser?.uid

        override suspend fun getAllFoodItems(
            onResult: (List<FoodItem>) -> Unit,
            onFailure: (Exception) -> Unit,
        ) {
            if (userId == null) return
            val householdId = householdService.getHouseholdId()

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
                .whereEqualTo("status", FoodStatus.ACTIVE)
                .get()
                .addOnSuccessListener { documents ->
                    val items =
                        documents.documents
                            .mapNotNull { it.toObject<FoodItem>() }
                            .map { foodItem ->
                                foodItem.copy(
                                    daysDifference =
                                        calculateDaysDifference(
                                            foodItem.expiryTimestamp,
                                        ),
                                )
                            }
                    onResult(items)
                }.addOnFailureListener(onFailure)
        }

        override suspend fun getStorageLocationItems(
            storageLocation: StorageLocation,
            foodItemList: MutableLiveData<List<FoodItem>>,
            onResult: (List<FoodItem>) -> Unit,
            onFailure: (Exception) -> Unit,
        ) {
            if (userId == null) return
            val householdId = householdService.getHouseholdId()

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
                .whereEqualTo("status", FoodStatus.ACTIVE)
                .get()
                .addOnSuccessListener { documents ->
                    val items =
                        documents.documents
                            .mapNotNull { it.toObject<FoodItem>() }
                            .map { foodItem ->
                                foodItem.copy(
                                    daysDifference =
                                        calculateDaysDifference(
                                            foodItem.expiryTimestamp,
                                        ),
                                )
                            }
                    onResult(items)
                }.addOnFailureListener(onFailure)
        }

        private fun calculateDaysDifference(expiryTimestamp: Long): Int {
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
            return ChronoUnit.DAYS.between(currentDate, expiryDate).toInt()
        }
    }

package com.freshkeeper.screens.householdSettings.viemodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.freshkeeper.model.Household
import com.freshkeeper.model.User
import com.freshkeeper.model.service.AccountService
import com.freshkeeper.screens.AppViewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class HouseholdSettingsViewModel
    @Inject
    constructor(
        private val accountService: AccountService,
        private val firestore: FirebaseFirestore,
    ) : AppViewModel() {
        private val _user = MutableStateFlow(User())
        val user: StateFlow<User> = _user.asStateFlow()

        private val _household = MutableStateFlow(Household())
        val household: StateFlow<Household> = _household.asStateFlow()

        init {
            launchCatching {
                _user.value = accountService.getUserProfile()
                loadHousehold()
            }
        }

        private suspend fun loadHousehold() {
            val userId = _user.value.id
            val snapshot =
                firestore
                    .collection("households")
                    .whereArrayContains("users", userId)
                    .get()
                    .await()

            val household = snapshot.documents.firstOrNull()?.toObject(Household::class.java)
            _household.value = household ?: Household()
        }

        fun onUpdateHouseholdNameClick(newName: String) {
            launchCatching {
                val householdId = _household.value.id
                firestore
                    .collection("households")
                    .document(householdId)
                    .update("name", newName)
                    .await()

                _household.value = _household.value.copy(name = newName)
            }
        }

        fun createHousehold(
            name: String,
            type: String,
        ) {
            launchCatching {
                val userId = _user.value.id
                val newHousehold =
                    Household(
                        id = firestore.collection("households").document().id,
                        type = type,
                        users = listOf(userId),
                        name = name,
                        createdAt = System.currentTimeMillis(),
                        invites = emptyList(),
                        ownerId = userId,
                    )

                try {
                    firestore
                        .collection("households")
                        .document(newHousehold.id)
                        .set(newHousehold)
                        .await()

                    firestore
                        .collection("users")
                        .document(userId)
                        .update("householdId", newHousehold.id)
                        .await()

                    val foodItemsQuerySnapshot =
                        firestore
                            .collection("foodItems")
                            .whereEqualTo("userId", userId)
                            .get()
                            .await()

                    val batch = firestore.batch()

                    foodItemsQuerySnapshot.documents.forEach { document ->
                        batch.update(document.reference, "householdId", newHousehold.id)
                    }

                    batch.commit().await()

                    _household.value = newHousehold
                } catch (e: Exception) {
                    Log.e("CreateHousehold", "Error when creating the household or updating foodItems", e)
                }
            }
        }

        fun onUpdateHouseholdTypeClick(newType: String) {
            launchCatching {
                val householdId = _household.value.id
                firestore
                    .collection("households")
                    .document(householdId)
                    .update("type", newType)
                    .await()

                _household.value = _household.value.copy(type = newType)
            }
        }

        fun onDeleteHousehold() {
            launchCatching {
                val householdId = _household.value.id
                Log.d("DeleteHousehold", "Deleting household with ID: $householdId")

                try {
                    firestore
                        .collection("households")
                        .document(householdId)
                        .delete()
                        .await()

                    val usersQuerySnapshot =
                        firestore
                            .collection("users")
                            .whereEqualTo("householdId", householdId)
                            .get()
                            .await()

                    val batch = firestore.batch()

                    usersQuerySnapshot.documents.forEach { document ->
                        batch.update(document.reference, "householdId", null)
                    }

                    batch.commit().await()

                    _household.value = Household()
                } catch (e: Exception) {
                    Log.e(
                        "DeleteHousehold",
                        "Error when deleting the household or updating the users",
                        e,
                    )
                }
            }
        }

        fun onLeaveHousehold() {
            launchCatching {
                val householdId = _household.value.id
                val userId = _user.value.id

                firestore
                    .collection("households")
                    .document(householdId)
                    .update("users", FieldValue.arrayRemove(userId))
                    .await()

                firestore
                    .collection("users")
                    .document(userId)
                    .update("householdId", null)
                    .await()

                _household.value = Household()
            }
        }

        fun addUserById(
            userId: String,
            context: Context,
            errorText: String,
            successText: String,
        ) {
            launchCatching {
                val householdId = _household.value.id

                val userSnapshot =
                    firestore
                        .collection("users")
                        .document(userId)
                        .get()
                        .await()
                if (!userSnapshot.exists()) {
                    Toast.makeText(context, errorText, Toast.LENGTH_SHORT).show()
                    return@launchCatching
                }

                firestore
                    .collection("households")
                    .document(householdId)
                    .update("users", FieldValue.arrayUnion(userId))
                    .await()

                firestore
                    .collection("users")
                    .document(userId)
                    .update("householdId", householdId)
                    .await()

                _household.value =
                    _household.value.copy(
                        users = _household.value.users + userId,
                    )

                Toast.makeText(context, successText, Toast.LENGTH_SHORT).show()
            }
        }

        fun joinHouseholdById(
            householdId: String,
            context: Context,
            errorText: String,
        ) {
            launchCatching {
                try {
                    val householdSnapshot =
                        firestore
                            .collection("households")
                            .document(householdId)
                            .get()
                            .await()

                    if (!householdSnapshot.exists()) {
                        Toast.makeText(context, errorText, Toast.LENGTH_SHORT).show()
                        return@launchCatching
                    }

                    val userId = _user.value.id

                    firestore
                        .collection("households")
                        .document(householdId)
                        .update("users", FieldValue.arrayUnion(userId))
                        .await()

                    firestore
                        .collection("users")
                        .document(userId)
                        .update("householdId", householdId)
                        .await()

                    val joinedHousehold = householdSnapshot.toObject(Household::class.java)
                    _household.value = joinedHousehold ?: Household()
                } catch (e: Exception) {
                    Log.e("JoinHousehold", "Error joining household with ID: $householdId", e)
                    Toast.makeText(context, errorText, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

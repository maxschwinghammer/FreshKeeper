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
                _user.value = accountService.getUserObject()
                loadHousehold()
            }
        }

        private suspend fun loadHousehold() {
            val user = _user.value
            val snapshot =
                firestore
                    .collection("households")
                    .whereArrayContains("users", user.id)
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
                val user = _user.value
                val newHousehold =
                    Household(
                        id = firestore.collection("households").document().id,
                        type = type,
                        users = listOf(user.id),
                        name = name,
                        createdAt = System.currentTimeMillis(),
                        invites = emptyList(),
                        ownerId = user.id,
                    )

                try {
                    firestore
                        .collection("households")
                        .document(newHousehold.id)
                        .set(newHousehold)
                        .await()

                    firestore
                        .collection("users")
                        .document(user.id)
                        .update("householdId", newHousehold.id)
                        .await()

                    val foodItemsQuerySnapshot =
                        firestore
                            .collection("foodItems")
                            .whereEqualTo("userId", user.id)
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

        fun onUpdateHouseholdTypeClick(
            newType: String,
            selectedUser: String?,
        ) {
            launchCatching {
                val householdId = _household.value.id
                val ownerId = _household.value.ownerId

                if (newType == "Single household") {
                    try {
                        val usersToRemove = _household.value.users.filter { it != ownerId && it != selectedUser }

                        val batch = firestore.batch()

                        batch.update(
                            firestore.collection("households").document(householdId),
                            "users",
                            FieldValue.arrayRemove(*usersToRemove.toTypedArray()),
                        )

                        usersToRemove.forEach { user ->
                            user.let {
                                batch.update(
                                    firestore.collection("users").document(it),
                                    "householdId",
                                    null,
                                )
                            }
                        }

                        batch.commit().await()

                        _household.value =
                            _household.value.copy(
                                users = listOfNotNull(_household.value.users.find { it == ownerId }),
                            )
                    } catch (e: Exception) {
                        Log.e("UpdateHouseholdType", "Error when updating users", e)
                    }
                } else if (newType == "Pair" && selectedUser != null) {
                    try {
                        val usersToRemove = _household.value.users.filter { it != ownerId && it != selectedUser }

                        val batch = firestore.batch()

                        batch.update(
                            firestore.collection("households").document(householdId),
                            "users",
                            FieldValue.arrayRemove(*usersToRemove.toTypedArray()),
                        )

                        usersToRemove.forEach { user ->
                            user.let {
                                batch.update(
                                    firestore.collection("users").document(it),
                                    "householdId",
                                    null,
                                )
                            }
                        }

                        batch.commit().await()

                        _household.value =
                            _household.value.copy(
                                users =
                                    listOfNotNull(
                                        _household.value.users.find { it == ownerId },
                                        _household.value.users.find { it == selectedUser },
                                    ),
                            )
                    } catch (e: Exception) {
                        Log.e("UpdateHouseholdType", "Error when updating users", e)
                    }
                }

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

                val user = userSnapshot.toObject(User::class.java)

                if (user != null) {
                    firestore
                        .collection("households")
                        .document(householdId)
                        .update("users", FieldValue.arrayUnion(user.id))
                        .await()
                }

                firestore
                    .collection("users")
                    .document(userId)
                    .update("householdId", householdId)
                    .await()

                if (user != null) {
                    _household.value =
                        _household.value.copy(
                            users = _household.value.users + user.id,
                        )
                }

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

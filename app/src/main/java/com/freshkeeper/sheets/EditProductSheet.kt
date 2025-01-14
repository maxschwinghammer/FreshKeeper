package com.freshkeeper.sheets

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.freshkeeper.R
import com.freshkeeper.model.Activity
import com.freshkeeper.model.FoodItem
import com.freshkeeper.model.User
import com.freshkeeper.model.service.AccountServiceImpl
import com.freshkeeper.screens.home.DropdownMenu
import com.freshkeeper.screens.home.ExpiryDatePicker
import com.freshkeeper.screens.home.UnitSelector
import com.freshkeeper.screens.profileSettings.viewmodel.ProfileSettingsViewModel
import com.freshkeeper.ui.theme.AccentGreenColor
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.RedColor
import com.freshkeeper.ui.theme.TextColor
import com.freshkeeper.ui.theme.WhiteColor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("ktlint:standard:function-naming")
@Composable
fun EditProductSheet(
    sheetState: SheetState,
    foodItem: FoodItem,
    profileSettingsViewModel: ProfileSettingsViewModel = hiltViewModel(),
) {
    val accountService = remember { AccountServiceImpl() }

    var productName by remember { mutableStateOf(foodItem.name) }
    var quantity by remember { mutableStateOf(foodItem.quantity.toString()) }
    val unit = remember { mutableStateOf(foodItem.unit) }
    val storageLocation = remember { mutableStateOf(foodItem.storageLocation) }
    val category = remember { mutableStateOf(foodItem.category) }
    var isConsumedChecked by remember { mutableStateOf(foodItem.consumed) }
    var isThrownAwayChecked by remember { mutableStateOf(foodItem.thrownAway) }
    val imageUrl by remember { mutableStateOf(foodItem.imageUrl) }

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    val storageLocationMap =
        mapOf(
            "fridge" to R.string.fridge,
            "cupboard" to R.string.cupboard,
            "freezer" to R.string.freezer,
            "counter_top" to R.string.counter_top,
            "cellar" to R.string.cellar,
            "bread_box" to R.string.bread_box,
            "spice_rack" to R.string.spice_rack,
            "pantry" to R.string.pantry,
            "fruit_basket" to R.string.fruit_basket,
            "other" to R.string.other,
        )

    val categoryMap =
        mapOf(
            "dairy_goods" to R.string.dairy_goods,
            "vegetables" to R.string.vegetables,
            "fruits" to R.string.fruits,
            "meat" to R.string.meat,
            "fish" to R.string.fish,
            "frozen_goods" to R.string.frozen_goods,
            "spices" to R.string.spices,
            "bread" to R.string.bread,
            "confectionery" to R.string.confectionery,
            "drinks" to R.string.drinks,
            "noodles" to R.string.noodles,
            "canned_goods" to R.string.canned_goods,
            "candy" to R.string.candy,
            "other" to R.string.other,
        )

    val storageLocationReverseMap =
        mapOf(
            R.string.fridge to "fridge",
            R.string.cupboard to "cupboard",
            R.string.freezer to "freezer",
            R.string.counter_top to "counter_top",
            R.string.cellar to "cellar",
            R.string.bread_box to "bread_box",
            R.string.spice_rack to "spice_rack",
            R.string.pantry to "pantry",
            R.string.fruit_basket to "fruit_basket",
            R.string.other to "other",
        )

    val categoryReverseMap =
        mapOf(
            R.string.dairy_goods to "dairy_goods",
            R.string.vegetables to "vegetables",
            R.string.fruits to "fruits",
            R.string.meat to "meat",
            R.string.fish to "fish",
            R.string.frozen_goods to "frozen_goods",
            R.string.spices to "spices",
            R.string.bread to "bread",
            R.string.confectionery to "confectionery",
            R.string.drinks to "drinks",
            R.string.noodles to "noodles",
            R.string.canned_goods to "canned_goods",
            R.string.candy to "candy",
            R.string.other to "other",
        )

    val selectedStorageLocation = storageLocationMap[storageLocation.value] ?: R.string.fridge
    val selectedCategory = categoryMap[category.value] ?: R.string.dairy_goods

    var expiryDate by remember { mutableLongStateOf(foodItem.expiryTimestamp) }
    val coroutineScope = rememberCoroutineScope()

    val user by profileSettingsViewModel.user.collectAsState(initial = User())

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.edit_product),
            color = TextColor,
            fontSize = 18.sp,
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = productName,
                    onValueChange = { productName = it },
                    label = { Text(stringResource(R.string.product_name)) },
                    colors =
                        OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = ComponentStrokeColor,
                            focusedBorderColor = AccentTurquoiseColor,
                            unfocusedLabelColor = TextColor,
                            focusedLabelColor = AccentTurquoiseColor,
                        ),
                    maxLines = 1,
                )

                Spacer(modifier = Modifier.height(8.dp))

                ExpiryDatePicker(
                    expiryDate = expiryDate,
                    onDateChange = { newDate ->
                        if (newDate != null) {
                            expiryDate = newDate
                        }
                    },
                )
            }

            if (imageUrl.isNotEmpty()) {
                Box(
                    modifier =
                        Modifier
                            .padding(top = 8.dp, start = 16.dp)
                            .defaultMinSize(minWidth = 150.dp, minHeight = 129.dp)
                            .weight(1f)
                            .heightIn(max = 129.dp),
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUrl),
                        contentDescription = "Product Image",
                        contentScale = ContentScale.Fit,
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp)),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = quantity,
                onValueChange = { if (it.matches(Regex("\\d{0,4}"))) quantity = it },
                label = { Text(stringResource(R.string.quantity)) },
                modifier = Modifier.weight(1f),
                colors =
                    OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = ComponentStrokeColor,
                        focusedBorderColor = AccentTurquoiseColor,
                        unfocusedLabelColor = TextColor,
                        focusedLabelColor = AccentTurquoiseColor,
                    ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                maxLines = 1,
            )

            Box(modifier = Modifier.weight(1f).padding(start = 6.dp)) {
                UnitSelector(unit = unit)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        DropdownMenu(
            selectedStorageLocation,
            onSelect = { selectedStorageLocation ->
                storageLocation.value = storageLocationReverseMap[selectedStorageLocation] ?: "fridge"
            },
            "storageLocations",
            stringResource(R.string.storage_location),
        )

        Spacer(modifier = Modifier.height(8.dp))

        DropdownMenu(
            selectedCategory,
            onSelect = { selectedCategory ->
                category.value = categoryReverseMap[selectedCategory] ?: "dairy_goods"
            },
            "categories",
            stringResource(R.string.category),
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = isConsumedChecked,
                onCheckedChange = {
                    isConsumedChecked = it
                    if (it) isThrownAwayChecked = false
                },
                enabled = !isThrownAwayChecked,
                colors =
                    CheckboxDefaults.colors(
                        checkmarkColor = ComponentBackgroundColor,
                        checkedColor = AccentGreenColor,
                    ),
            )
            Text(
                stringResource(R.string.mark_as_consumed),
                color = TextColor,
                modifier = Modifier.padding(start = 8.dp),
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = isThrownAwayChecked,
                onCheckedChange = {
                    isThrownAwayChecked = it
                    if (it) isConsumedChecked = false
                },
                enabled = !isConsumedChecked,
                colors =
                    CheckboxDefaults.colors(
                        checkmarkColor = ComponentBackgroundColor,
                        checkedColor = RedColor,
                    ),
            )
            Text(
                stringResource(R.string.mark_as_thrown_away),
                color = TextColor,
                modifier = Modifier.padding(start = 8.dp),
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val userId = auth.currentUser?.uid ?: return@Button
                val userRef = firestore.collection("users").document(userId)

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
                                    "quantity" to quantity.toInt(),
                                    "unit" to unit.value,
                                    "storageLocation" to storageLocation.value,
                                    "category" to category.value,
                                    "expiryTimestamp" to expiryDate,
                                    "consumed" to isConsumedChecked,
                                    "thrownAway" to isThrownAwayChecked,
                                )

                            document.reference
                                .update(updates)
                                .addOnSuccessListener {
                                    Log.d("Firestore", "Product updated successfully")
                                    coroutineScope.launch {
                                        sheetState.hide()
                                    }
                                }.addOnFailureListener { e ->
                                    Log.w("Firestore", "Error when updating the product", e)
                                }

                            userRef.get().addOnSuccessListener {
                                val userName = user.displayName

                                val activityType: String
                                val activityText: String

                                if (isConsumedChecked) {
                                    activityType = "remove"
                                    activityText = "$userName marked $productName as consumed"
                                } else if (isThrownAwayChecked) {
                                    activityType = "remove"
                                    activityText = "$userName marked $productName as thrown away"
                                } else if (productName != foodItem.name) {
                                    activityType = "edit"
                                    activityText = "$userName edited the name of '${foodItem.name}' to '$productName'"
                                } else {
                                    val changes = mutableListOf<String>()

                                    if (expiryDate != foodItem.expiryTimestamp) changes.add("expiry date")
                                    if (quantity.toInt() != foodItem.quantity) changes.add("quantity")
                                    if (unit.value != foodItem.unit) changes.add("unit")
                                    if (storageLocation.value != foodItem.storageLocation) changes.add("storage location")
                                    if (category.value != foodItem.category) changes.add("category")

                                    activityType =
                                        if (changes.size > 1) {
                                            "edit"
                                        } else if (changes.size == 1) {
                                            "update"
                                        } else {
                                            "edit"
                                        }

                                    activityText =
                                        if (changes.size > 1) {
                                            "$userName edited the product $productName"
                                        } else if (changes.size == 1) {
                                            "$userName updated the ${changes.first()} of '$productName'"
                                        } else {
                                            "$userName edited the product $productName"
                                        }
                                }

                                coroutineScope.launch {
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
                                            Log.w("Firestore", "Error when adding the activity", e)
                                        }
                                }
                            }
                        } else {
                            Log.w("Firestore", "No document found with the given ID")
                        }
                    }.addOnFailureListener { e ->
                        Log.w("Firestore", "Error retrieving document", e)
                    }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = WhiteColor),
        ) {
            Text(stringResource(R.string.save_changes), color = ComponentBackgroundColor)
        }
    }
}

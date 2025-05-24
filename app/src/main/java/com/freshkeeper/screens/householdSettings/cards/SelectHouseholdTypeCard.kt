package com.freshkeeper.screens.householdSettings.cards

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.freshkeeper.R
import com.freshkeeper.model.FoodItem
import com.freshkeeper.model.Household
import com.freshkeeper.model.HouseholdType
import com.freshkeeper.model.User
import com.freshkeeper.screens.profileSettings.cards.AccountCenterCard
import com.freshkeeper.screens.profileSettings.cards.card
import com.freshkeeper.service.householdTypeReverseMap
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.GreyColor
import com.freshkeeper.ui.theme.TextColor
import com.freshkeeper.ui.theme.WhiteColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun SelectHouseholdTypeCard(
    selectedHouseholdType: HouseholdType,
    onHouseholdTypeSelected: (HouseholdType, String?) -> Unit,
    onDeleteProducts: () -> Unit,
    onAddProducts: () -> Unit,
    household: Household,
    user: User,
    items: List<FoodItem>,
) {
    var showHouseholdTypeDialog by remember { mutableStateOf(false) }
    var showWarningDialog by remember { mutableStateOf(false) }
    var showSingleHouseholdWarningDialog by remember { mutableStateOf(false) }
    var showAddProductsDialog by remember { mutableStateOf(false) }

    var selectedType by remember { mutableStateOf(selectedHouseholdType) }
    var selectedUser by remember { mutableStateOf<String?>(null) }

    val isUserOwner by remember { mutableStateOf(household.ownerId == user.id) }
    val context = LocalContext.current
    val typeChangeError = stringResource(R.string.type_change_error)

    val householdTypes =
        listOf(
            HouseholdType.FAMILY,
            HouseholdType.SHARED_APARTMENT,
            HouseholdType.SINGLE,
            HouseholdType.PAIR,
        )

    AccountCenterCard(
        title =
            stringResource(R.string.household_type) + ": " +
                householdTypeReverseMap[household.type]?.let { stringResource(it) },
        icon = if (isUserOwner) Icons.Filled.Edit else null,
        modifier =
            Modifier
                .card()
                .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp)),
        onCardClick = {
            if (isUserOwner) {
                showHouseholdTypeDialog = true
            } else {
                Toast.makeText(context, typeChangeError, Toast.LENGTH_SHORT).show()
            }
        },
    )

    if (showHouseholdTypeDialog) {
        AlertDialog(
            containerColor = ComponentBackgroundColor,
            title = { Text(stringResource(R.string.select_household_type)) },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    householdTypes.forEach { type ->
                        val label =
                            householdTypeReverseMap[type]?.let { stringResource(it) } ?: type.name
                        val borderColor =
                            if (selectedType == type) {
                                AccentTurquoiseColor
                            } else {
                                Color.Transparent
                            }
                        Button(
                            onClick = { selectedType = type },
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor = GreyColor,
                                    contentColor = TextColor,
                                ),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, borderColor),
                            modifier =
                                Modifier
                                    .padding(vertical = 2.dp)
                                    .align(Alignment.CenterHorizontally),
                        ) {
                            Text(text = label)
                        }
                    }
                }
            },
            dismissButton = {
                Button(
                    onClick = { showHouseholdTypeDialog = false },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = GreyColor,
                            contentColor = TextColor,
                        ),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, ComponentStrokeColor),
                ) {
                    Text(text = stringResource(R.string.cancel))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        when {
                            selectedType == HouseholdType.PAIR && household.users.size > 2 -> {
                                showWarningDialog = true
                            }
                            selectedType == HouseholdType.SINGLE && household.users.size > 1 -> {
                                showSingleHouseholdWarningDialog = true
                            }
                            else -> {
                                onHouseholdTypeSelected(selectedType, null)
                            }
                        }
                        showHouseholdTypeDialog = false
                        if (items.isNotEmpty()) {
                            showAddProductsDialog = true
                        }
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = WhiteColor,
                            contentColor = GreyColor,
                        ),
                    shape = RoundedCornerShape(20.dp),
                ) {
                    Text(text = stringResource(R.string.update))
                }
            },
            onDismissRequest = { showHouseholdTypeDialog = false },
        )
    }

    if (showWarningDialog) {
        AlertDialog(
            containerColor = ComponentBackgroundColor,
            title = { Text(stringResource(R.string.warning)) },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = stringResource(R.string.pair_selection_info),
                        style = MaterialTheme.typography.titleMedium,
                        color = TextColor,
                        modifier = Modifier.padding(8.dp),
                    )
                    household.users.forEach { user ->
                        val borderColor =
                            if (selectedUser == user) {
                                AccentTurquoiseColor
                            } else {
                                Color.Transparent
                            }
                        Button(
                            onClick = { selectedUser = user },
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor = GreyColor,
                                    contentColor = TextColor,
                                ),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, borderColor),
                            modifier =
                                Modifier.padding(vertical = 2.dp).align(
                                    Alignment.CenterHorizontally,
                                ),
                        ) {
                            Text(text = user)
                        }
                    }
                }
            },
            dismissButton = {
                Button(
                    onClick = { showWarningDialog = false },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = GreyColor,
                            contentColor = TextColor,
                        ),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, ComponentStrokeColor),
                ) {
                    Text(text = stringResource(R.string.cancel))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (selectedUser != null) {
                            onHouseholdTypeSelected(selectedType, selectedUser)
                        }
                        showWarningDialog = false
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = WhiteColor,
                            contentColor = GreyColor,
                        ),
                    shape = RoundedCornerShape(20.dp),
                ) {
                    Text(text = stringResource(R.string.confirm))
                }
            },
            onDismissRequest = { showWarningDialog = false },
        )
    }

    if (showSingleHouseholdWarningDialog) {
        AlertDialog(
            containerColor = ComponentBackgroundColor,
            title = { Text(stringResource(R.string.warning)) },
            text = {
                Text(text = stringResource(R.string.single_household_warning))
            },
            dismissButton = {
                Button(
                    onClick = { showSingleHouseholdWarningDialog = false },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = GreyColor,
                            contentColor = TextColor,
                        ),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, ComponentStrokeColor),
                ) {
                    Text(text = stringResource(R.string.cancel))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onHouseholdTypeSelected(HouseholdType.SINGLE, null)
                        showSingleHouseholdWarningDialog = false
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = WhiteColor,
                            contentColor = GreyColor,
                        ),
                    shape = RoundedCornerShape(20.dp),
                ) {
                    Text(text = stringResource(R.string.confirm))
                }
            },
            onDismissRequest = { showSingleHouseholdWarningDialog = false },
        )
    }
    if (showAddProductsDialog) {
        AlertDialog(
            containerColor = ComponentBackgroundColor,
            title = { Text(stringResource(R.string.add_products)) },
            text = {
                Text(text = stringResource(R.string.add_products_warning))
            },
            dismissButton = {
                Button(
                    onClick = {
                        onDeleteProducts()
                        showAddProductsDialog = false
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = GreyColor,
                            contentColor = TextColor,
                        ),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, ComponentStrokeColor),
                ) {
                    Text(text = stringResource(R.string.no))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onAddProducts()
                        showAddProductsDialog = false
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = WhiteColor,
                            contentColor = GreyColor,
                        ),
                    shape = RoundedCornerShape(20.dp),
                ) {
                    Text(text = stringResource(R.string.yes))
                }
            },
            onDismissRequest = { showAddProductsDialog = false },
        )
    }
}

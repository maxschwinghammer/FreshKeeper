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
import com.freshkeeper.model.Household
import com.freshkeeper.model.User
import com.freshkeeper.screens.profileSettings.cards.AccountCenterCard
import com.freshkeeper.screens.profileSettings.cards.card
import com.freshkeeper.service.reverseHouseholdTypeMap
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.GreyColor
import com.freshkeeper.ui.theme.TextColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun SelectHouseholdTypeCard(
    selectedHouseholdType: String,
    onHouseholdTypeSelected: (String, String?) -> Unit,
    household: Household,
    user: User,
) {
    var showHouseholdTypeDialog by remember { mutableStateOf(false) }
    var showWarningDialog by remember { mutableStateOf(false) }
    var showSingleHouseholdWarningDialog by remember { mutableStateOf(false) }

    var selectedType by remember { mutableStateOf(selectedHouseholdType) }
    var selectedUser by remember { mutableStateOf<String?>(null) }

    val isUserOwner by remember { mutableStateOf(household.ownerId == user.id) }
    val context = LocalContext.current

    val householdTypeMap =
        mapOf(
            stringResource(R.string.family) to "Family",
            stringResource(R.string.shared_apartment) to "Shared apartment",
            stringResource(R.string.single_household) to "Single household",
            stringResource(R.string.pair) to "Pair",
        )

    val typeChangeError = stringResource(R.string.type_change_error)

    AccountCenterCard(
        title =
            stringResource(R.string.household_type) + ": " +
                reverseHouseholdTypeMap[household.type]?.let { stringResource(it) },
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
                    listOf(
                        stringResource(R.string.family),
                        stringResource(R.string.shared_apartment),
                        stringResource(R.string.single_household),
                        stringResource(R.string.pair),
                    ).forEach { type ->
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
                            Text(text = type)
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
                        val englishType = householdTypeMap[selectedType] ?: selectedType
                        when {
                            englishType == "Pair" && household.users.size > 2 -> {
                                showWarningDialog = true
                            }
                            englishType == "Single household" && household.users.size > 1 -> {
                                showSingleHouseholdWarningDialog = true
                            }
                            else -> {
                                onHouseholdTypeSelected(englishType, null)
                            }
                        }
                        showHouseholdTypeDialog = false
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = AccentTurquoiseColor,
                            contentColor = GreyColor,
                        ),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, ComponentStrokeColor),
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
                            containerColor = AccentTurquoiseColor,
                            contentColor = GreyColor,
                        ),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, ComponentStrokeColor),
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
                        onHouseholdTypeSelected("Single household", null)
                        showSingleHouseholdWarningDialog = false
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = AccentTurquoiseColor,
                            contentColor = GreyColor,
                        ),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, ComponentStrokeColor),
                ) {
                    Text(text = stringResource(R.string.confirm))
                }
            },
            onDismissRequest = { showSingleHouseholdWarningDialog = false },
        )
    }
}

package com.freshkeeper.screens.householdSettings.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.freshkeeper.R
import com.freshkeeper.model.FoodItem
import com.freshkeeper.model.HouseholdType
import com.freshkeeper.screens.profileSettings.cards.AccountCenterCard
import com.freshkeeper.screens.profileSettings.cards.card
import com.freshkeeper.service.householdTypeMap
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.GreyColor
import com.freshkeeper.ui.theme.TextColor
import com.freshkeeper.ui.theme.WhiteColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun CreateHouseholdCard(
    onCreateHouseholdClick: (String, HouseholdType) -> Unit,
    onAddProducts: () -> Unit,
    onDeleteProducts: () -> Unit,
    items: List<FoodItem>,
) {
    var showCreateHouseholdDialog by remember { mutableStateOf(false) }
    var showHouseholdTypeDialog by remember { mutableStateOf(false) }
    var showAddProductsDialog by remember { mutableStateOf(false) }

    var householdName by remember { mutableStateOf("") }
    var householdType by remember { mutableStateOf(HouseholdType.SINGLE) }

    AccountCenterCard(
        title = stringResource(R.string.create_household),
        icon = painterResource(R.drawable.plus),
        modifier =
            Modifier
                .card()
                .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp)),
        onCardClick = { showCreateHouseholdDialog = true },
    )

    if (showCreateHouseholdDialog) {
        AlertDialog(
            containerColor = ComponentBackgroundColor,
            title = { Text(stringResource(R.string.create_household)) },
            text = {
                Column {
                    TextField(
                        value = householdName,
                        colors =
                            TextFieldDefaults.colors(
                                focusedTextColor = TextColor,
                                unfocusedTextColor = TextColor,
                                focusedContainerColor = GreyColor,
                                unfocusedContainerColor = GreyColor,
                                focusedIndicatorColor = AccentTurquoiseColor,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                        onValueChange = { householdName = it },
                        placeholder = { Text(text = stringResource(R.string.household_name)) },
                    )
                }
            },
            dismissButton = {
                Button(
                    onClick = { showCreateHouseholdDialog = false },
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
                        showCreateHouseholdDialog = false
                        showHouseholdTypeDialog = true
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = WhiteColor,
                            contentColor = GreyColor,
                        ),
                    enabled =
                        householdName.isNotEmpty() &&
                            householdName.all
                                { it.isLetter() || it.isWhitespace() },
                    shape = RoundedCornerShape(20.dp),
                ) {
                    Text(text = stringResource(R.string.create))
                }
            },
            onDismissRequest = { showCreateHouseholdDialog = false },
        )
    }

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
                    householdTypeMap.forEach { (localName, type) ->
                        val borderColor =
                            if (householdType == type) {
                                AccentTurquoiseColor
                            } else {
                                Color.Transparent
                            }
                        Button(
                            onClick = { householdType = type },
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
                            Text(text = stringResource(localName))
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
                        onCreateHouseholdClick(householdName, householdType)
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
                    Text(text = stringResource(R.string.confirm))
                }
            },
            onDismissRequest = { showHouseholdTypeDialog = false },
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

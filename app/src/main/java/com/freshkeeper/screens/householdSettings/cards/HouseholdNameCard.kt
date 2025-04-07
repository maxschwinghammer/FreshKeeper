package com.freshkeeper.screens.householdSettings.cards

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
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
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.GreyColor
import com.freshkeeper.ui.theme.TextColor
import com.freshkeeper.ui.theme.WhiteColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun HouseholdNameCard(
    onUpdateHouseholdNameClick: (String) -> Unit,
    household: Household,
    user: User,
) {
    var showHouseholdNameDialog by remember { mutableStateOf(false) }
    var householdName by remember { mutableStateOf(household.name) }
    val cardTitle = household.name.ifBlank { stringResource(R.string.household_name) }
    val isUserOwner by remember { mutableStateOf(household.ownerId == user.id) }
    val nameChangeError = stringResource(R.string.name_change_error)
    val context = LocalContext.current

    AccountCenterCard(
        "Name: $cardTitle",
        icon = if (isUserOwner) Icons.Filled.Edit else null,
        modifier =
            Modifier
                .card()
                .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp)),
    ) {
        if (isUserOwner) {
            householdName = household.name
            showHouseholdNameDialog = true
        } else {
            Toast.makeText(context, nameChangeError, Toast.LENGTH_SHORT).show()
        }
    }

    if (showHouseholdNameDialog) {
        AlertDialog(
            containerColor = ComponentBackgroundColor,
            title = { Text(stringResource(R.string.household_name)) },
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
                    )
                }
            },
            dismissButton = {
                Button(
                    onClick = { showHouseholdNameDialog = false },
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
                        onUpdateHouseholdNameClick(householdName)
                        showHouseholdNameDialog = false
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
            onDismissRequest = { showHouseholdNameDialog = false },
        )
    }
}

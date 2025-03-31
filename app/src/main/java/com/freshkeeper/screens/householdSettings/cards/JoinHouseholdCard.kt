package com.freshkeeper.screens.householdSettings.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.freshkeeper.R
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
fun JoinHouseholdCard(onJoinHouseholdClick: (String) -> Unit) {
    var showJoinHouseholdDialog by remember { mutableStateOf(false) }
    var householdId by remember { mutableStateOf("") }

    AccountCenterCard(
        title = stringResource(R.string.join_household),
        icon = painterResource(R.drawable.user_joined),
        modifier =
            Modifier
                .card()
                .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp)),
        onCardClick = { showJoinHouseholdDialog = true },
    )

    if (showJoinHouseholdDialog) {
        AlertDialog(
            containerColor = ComponentBackgroundColor,
            title = { Text(stringResource(R.string.join_household)) },
            text = {
                Column {
                    TextField(
                        value = householdId,
                        colors =
                            TextFieldDefaults.colors(
                                focusedTextColor = TextColor,
                                unfocusedTextColor = TextColor,
                                focusedContainerColor = GreyColor,
                                unfocusedContainerColor = GreyColor,
                                focusedIndicatorColor = AccentTurquoiseColor,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                        onValueChange = { householdId = it },
                        placeholder = { Text(text = stringResource(R.string.household_id)) },
                    )
                }
            },
            dismissButton = {
                Button(
                    onClick = { showJoinHouseholdDialog = false },
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
                        onJoinHouseholdClick(householdId)
                        showJoinHouseholdDialog = false
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = WhiteColor,
                            contentColor = GreyColor,
                        ),
                    enabled = householdId.length == 20,
                    shape = RoundedCornerShape(20.dp),
                ) {
                    Text(text = stringResource(R.string.join))
                }
            },
            onDismissRequest = { showJoinHouseholdDialog = false },
        )
    }
}

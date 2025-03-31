package com.freshkeeper.screens.householdSettings.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.freshkeeper.R
import com.freshkeeper.screens.profileSettings.cards.AccountCenterCard
import com.freshkeeper.screens.profileSettings.cards.card
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.GreyColor
import com.freshkeeper.ui.theme.RedColor
import com.freshkeeper.ui.theme.TextColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun DeleteHouseholdCard(onDeleteHouseholdClick: () -> Unit) {
    var showDeleteHouseholdDialog by remember { mutableStateOf(false) }

    AccountCenterCard(
        stringResource(R.string.delete_household),
        Icons.Filled.Delete,
        Modifier.card().border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp)),
    ) {
        showDeleteHouseholdDialog = true
    }

    if (showDeleteHouseholdDialog) {
        AlertDialog(
            containerColor = ComponentBackgroundColor,
            title = { Text(stringResource(R.string.delete_household)) },
            text = { Text(stringResource(R.string.delete_household_description), color = TextColor) },
            dismissButton = {
                Button(
                    onClick = { showDeleteHouseholdDialog = false },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = GreyColor,
                            contentColor = TextColor,
                        ),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, ComponentStrokeColor),
                ) {
                    Text(text = stringResource(R.string.cancel), color = TextColor)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteHouseholdClick()
                        showDeleteHouseholdDialog = false
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = RedColor,
                            contentColor = TextColor,
                        ),
                    shape = RoundedCornerShape(20.dp),
                ) {
                    Text(text = stringResource(R.string.confirm))
                }
            },
            onDismissRequest = { showDeleteHouseholdDialog = false },
        )
    }
}

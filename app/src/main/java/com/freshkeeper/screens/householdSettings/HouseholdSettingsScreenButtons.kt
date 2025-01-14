package com.freshkeeper.screens.householdSettings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.freshkeeper.R
import com.freshkeeper.screens.profileSettings.AccountCenterCard
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.GreyColor
import com.freshkeeper.ui.theme.RedColor
import com.freshkeeper.ui.theme.TextColor
import kotlinx.coroutines.launch

fun Modifier.card(): Modifier = this.padding(16.dp, 0.dp, 16.dp, 0.dp)

@Suppress("ktlint:standard:function-naming")
@Composable
fun CreateHouseholdCard(onCreateHouseholdClick: (String, String) -> Unit) {
    var showCreateHouseholdDialog by remember { mutableStateOf(false) }
    var householdName by remember { mutableStateOf("") }
    var showHouseholdTypeDialog by remember { mutableStateOf(false) }
    var householdType by remember { mutableStateOf("Single household") }

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
                            containerColor = AccentTurquoiseColor,
                            contentColor = TextColor,
                        ),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, ComponentStrokeColor),
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
                    listOf("Family", "Shared Apartment", "Single Household", "Pair").forEach { type ->
                        val borderColor = if (householdType == type) AccentTurquoiseColor else Color.Transparent
                        Button(
                            onClick = { householdType = type },
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor = GreyColor,
                                    contentColor = TextColor,
                                ),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, borderColor),
                            modifier = Modifier.padding(vertical = 2.dp).align(Alignment.CenterHorizontally),
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
                        onCreateHouseholdClick(householdName, householdType)
                        showHouseholdTypeDialog = false
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = AccentTurquoiseColor,
                            contentColor = TextColor,
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
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun HouseholdNameCard(
    householdName: String,
    onUpdateHouseholdNameClick: (String) -> Unit,
) {
    var showHouseholdNameDialog by remember { mutableStateOf(false) }
    var newHouseholdName by remember { mutableStateOf(householdName) }
    val cardTitle = householdName.ifBlank { stringResource(R.string.household_name) }

    AccountCenterCard(
        "Name: $cardTitle",
        Icons.Filled.Edit,
        Modifier
            .card()
            .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp)),
    ) {
        newHouseholdName = householdName
        showHouseholdNameDialog = true
    }

    if (showHouseholdNameDialog) {
        AlertDialog(
            containerColor = ComponentBackgroundColor,
            title = { Text(stringResource(R.string.household_name)) },
            text = {
                Column {
                    TextField(
                        value = newHouseholdName,
                        colors =
                            TextFieldDefaults.colors(
                                focusedTextColor = TextColor,
                                unfocusedTextColor = TextColor,
                                focusedContainerColor = GreyColor,
                                unfocusedContainerColor = GreyColor,
                                focusedIndicatorColor = AccentTurquoiseColor,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                        onValueChange = { newHouseholdName = it },
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
                        onUpdateHouseholdNameClick(newHouseholdName)
                        showHouseholdNameDialog = false
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = AccentTurquoiseColor,
                            contentColor = TextColor,
                        ),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, ComponentStrokeColor),
                ) {
                    Text(text = stringResource(R.string.update))
                }
            },
            onDismissRequest = { showHouseholdNameDialog = false },
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun SelectHouseholdTypeCard(
    householdType: String,
    selectedHouseholdType: String,
    onHouseholdTypeSelected: (String) -> Unit,
) {
    var showHouseholdTypeDialog by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf(selectedHouseholdType) }

    AccountCenterCard(
        title = stringResource(R.string.household_type) + ": " + householdType,
        icon = Icons.Filled.Edit,
        modifier =
            Modifier
                .card()
                .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp)),
        onCardClick = { showHouseholdTypeDialog = true },
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
                    listOf("Family", "Shared Apartment", "Single Household", "Pair").forEach { type ->
                        val borderColor = if (selectedType == type) AccentTurquoiseColor else Color.Transparent
                        Button(
                            onClick = { selectedType = type },
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor = GreyColor,
                                    contentColor = TextColor,
                                ),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, borderColor),
                            modifier = Modifier.padding(vertical = 2.dp).align(Alignment.CenterHorizontally),
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
                        onHouseholdTypeSelected(selectedType)
                        showHouseholdTypeDialog = false
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = AccentTurquoiseColor,
                            contentColor = TextColor,
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
}

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
                    border = BorderStroke(1.dp, ComponentStrokeColor),
                ) {
                    Text(text = stringResource(R.string.delete_household), color = TextColor)
                }
            },
            onDismissRequest = { showDeleteHouseholdDialog = false },
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun LeaveHouseholdCard(onLeaveHouseholdClick: () -> Unit) {
    var showLeaveHouseholdDialog by remember { mutableStateOf(false) }

    AccountCenterCard(
        stringResource(R.string.leave_household),
        painterResource(R.drawable.leave),
        Modifier.card().border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp)),
    ) {
        showLeaveHouseholdDialog = true
    }

    if (showLeaveHouseholdDialog) {
        AlertDialog(
            containerColor = ComponentBackgroundColor,
            title = { Text(stringResource(R.string.leave_household)) },
            text = { Text(stringResource(R.string.leave_household_description), color = TextColor) },
            dismissButton = {
                Button(
                    onClick = { showLeaveHouseholdDialog = false },
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
                        onLeaveHouseholdClick()
                        showLeaveHouseholdDialog = false
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = RedColor,
                            contentColor = TextColor,
                        ),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, ComponentStrokeColor),
                ) {
                    Text(text = stringResource(R.string.leave_household), color = TextColor)
                }
            },
            onDismissRequest = { showLeaveHouseholdDialog = false },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("ktlint:standard:function-naming")
@Composable
fun InviteCard(inviteSheetState: SheetState) {
    val coroutineScope = rememberCoroutineScope()

    AccountCenterCard(
        title = stringResource(R.string.invite_people),
        icon = painterResource(R.drawable.invite),
        modifier =
            Modifier
                .card()
                .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp)),
        onCardClick = {
            coroutineScope.launch {
                inviteSheetState.show()
            }
        },
    )
}

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
                            containerColor = AccentTurquoiseColor,
                            contentColor = TextColor,
                        ),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, ComponentStrokeColor),
                ) {
                    Text(text = stringResource(R.string.join))
                }
            },
            onDismissRequest = { showJoinHouseholdDialog = false },
        )
    }
}

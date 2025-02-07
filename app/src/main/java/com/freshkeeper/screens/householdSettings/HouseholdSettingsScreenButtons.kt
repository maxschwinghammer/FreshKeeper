package com.freshkeeper.screens.householdSettings

import android.content.ClipData.newPlainText
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.freshkeeper.R
import com.freshkeeper.model.Household
import com.freshkeeper.model.User
import com.freshkeeper.screens.profileSettings.AccountCenterCard
import com.freshkeeper.screens.profileSettings.card
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.GreyColor
import com.freshkeeper.ui.theme.RedColor
import com.freshkeeper.ui.theme.TextColor
import kotlinx.coroutines.launch

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
                    enabled =
                        householdName.isNotEmpty() &&
                            householdName.all
                                { it.isLetter() || it.isWhitespace() },
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
                    listOf(
                        stringResource(R.string.family),
                        stringResource(R.string.shared_apartment),
                        stringResource(R.string.single_household),
                        stringResource(R.string.pair),
                    ).forEach { type ->
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
        title = stringResource(R.string.household_type) + ": " + household.type,
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
                        val borderColor = if (selectedUser == user) AccentTurquoiseColor else Color.Transparent
                        Button(
                            onClick = { selectedUser = user },
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor = GreyColor,
                                    contentColor = TextColor,
                                ),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, borderColor),
                            modifier = Modifier.padding(vertical = 2.dp).align(Alignment.CenterHorizontally),
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
                            contentColor = TextColor,
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
                            contentColor = TextColor,
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
fun InviteCard(
    inviteSheetState: SheetState,
    household: Household,
) {
    val coroutineScope = rememberCoroutineScope()
    val title =
        if (household.type == "Pair") {
            stringResource(R.string.invite_partner)
        } else {
            stringResource(R.string.invite_people)
        }

    AccountCenterCard(
        title = title,
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
                    enabled = householdId.length == 20,
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

@Suppress("ktlint:standard:function-naming")
@Composable
fun HouseholdIdCard(householdId: String) {
    val context = LocalContext.current
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    AccountCenterCard(
        title = stringResource(R.string.household_id) + ":\n" + householdId,
        icon = painterResource(R.drawable.copy),
        modifier =
            Modifier
                .card()
                .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp)),
    ) {
        val clip = newPlainText("Household ID", householdId)
        clipboardManager.setPrimaryClip(clip)
    }
}

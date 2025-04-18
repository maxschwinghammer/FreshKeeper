package com.freshkeeper.screens.authentication.signUp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.freshkeeper.R
import com.freshkeeper.screens.profile.viewmodel.ProfileViewModel
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.FreshKeeperTheme
import com.freshkeeper.ui.theme.GreyColor
import com.freshkeeper.ui.theme.RedColor
import com.freshkeeper.ui.theme.TextColor
import com.freshkeeper.ui.theme.WhiteColor
import com.google.firebase.auth.FirebaseAuth

@Suppress("ktlint:standard:function-naming")
@Composable
fun NameInputScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    var name by remember { mutableStateOf("") }
    var skipName by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val profileViewModel: ProfileViewModel = hiltViewModel()
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    if (showError) {
        errorMessage =
            if (name.isEmpty()) {
                stringResource(R.string.error_empty_name)
            } else if (name.length < 3) {
                stringResource(R.string.error_name_too_short)
            } else if (!name.matches("^[A-Za-z ]+$".toRegex())) {
                stringResource(R.string.error_name_invalid_characters)
            } else {
                ""
            }
    }

    FreshKeeperTheme {
        Scaffold {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(it),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    modifier = modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_transparent),
                        contentDescription = "Logo",
                        modifier = modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.padding(12.dp))
                    Text(
                        text = stringResource(R.string.sign_up_step2),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextColor,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.padding(12.dp))
                    Text(
                        text = stringResource(R.string.enter_name),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextColor,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.padding(12.dp))
                    TextField(
                        value = name,
                        onValueChange = { newName ->
                            if (newName.matches("^[A-Za-z ]*$".toRegex())) {
                                name = newName
                            }
                        },
                        colors =
                            TextFieldDefaults.colors(
                                unfocusedLabelColor = AccentTurquoiseColor,
                                focusedLabelColor = AccentTurquoiseColor,
                                cursorColor = AccentTurquoiseColor,
                                focusedTextColor = TextColor,
                                unfocusedTextColor = TextColor,
                                focusedContainerColor = GreyColor,
                                unfocusedContainerColor = GreyColor,
                                focusedIndicatorColor = AccentTurquoiseColor,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                        modifier =
                            Modifier.fillMaxWidth().padding(
                                start = 16.dp,
                                end = 16.dp,
                            ),
                    )
                    if (errorMessage.isEmpty()) {
                        Spacer(Modifier.padding(16.dp))
                    }

                    if (showError && errorMessage.isNotEmpty()) {
                        Spacer(Modifier.padding(8.dp))
                        Text(
                            text = errorMessage,
                            color = RedColor,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                        )
                        Spacer(Modifier.padding(8.dp))
                    }
                    Button(
                        onClick = {
                            skipName = true
                            navController.navigate("selectProfilePicture") {
                                launchSingleTop = true
                            }
                        },
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = WhiteColor,
                            ),
                        modifier = Modifier.fillMaxWidth().padding(16.dp, 0.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.skip),
                            fontSize = 16.sp,
                            color = ComponentBackgroundColor,
                        )
                    }
                    Spacer(Modifier.padding(6.dp))
                    Button(
                        onClick = {
                            showError = true
                            if (!skipName &&
                                name.length >= 3 &&
                                name.matches("^[A-Za-z ]+$".toRegex())
                            ) {
                                if (userId != null) {
                                    profileViewModel.updateDisplayName(name)
                                }
                                navController.navigate("selectProfilePicture") {
                                    launchSingleTop = true
                                }
                            }
                        },
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = WhiteColor,
                                disabledContainerColor = WhiteColor,
                            ),
                        modifier = Modifier.fillMaxWidth().padding(16.dp, 0.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.continue_text),
                            fontSize = 16.sp,
                            color = ComponentBackgroundColor,
                        )
                    }
                }
            }
        }
    }
}

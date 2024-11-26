package com.freshkeeper.screens.authentication.signUp

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.freshkeeper.R
import com.freshkeeper.screens.authentication.AuthenticationButton
import com.freshkeeper.screens.authentication.launchCredManBottomSheet
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.FreshKeeperTheme
import com.freshkeeper.ui.theme.LightGreyColor
import com.freshkeeper.ui.theme.RedColor
import com.freshkeeper.ui.theme.TextColor
import com.freshkeeper.ui.theme.WhiteColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun SignUpScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    val email = viewModel.email.collectAsState()
    val password = viewModel.password.collectAsState()
    val confirmPassword = viewModel.confirmPassword.collectAsState()
    val errorMessage = viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        launchCredManBottomSheet(context) { result ->
            Log.d("CredentialManager", "Received credential: $result")
            viewModel.onSignUpWithGoogle(result, navController)
        }
    }

    FreshKeeperTheme {
        Scaffold { it ->
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(it),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    modifier =
                        modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_transparent),
                        contentDescription = "Auth image",
                        modifier =
                            modifier
                                .fillMaxWidth()
                                .padding(16.dp, 4.dp),
                    )

                    Text(
                        text = stringResource(R.string.sign_up_header),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextColor,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(Modifier.padding(12.dp))

                    OutlinedTextField(
                        singleLine = true,
                        modifier =
                            modifier
                                .fillMaxWidth()
                                .padding(16.dp, 4.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .border(2.dp, ComponentStrokeColor, RoundedCornerShape(20.dp)),
                        value = email.value,
                        onValueChange = { viewModel.updateEmail(it) },
                        placeholder = { Text(stringResource(R.string.email)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email",
                            )
                        },
                    )

                    OutlinedTextField(
                        singleLine = true,
                        modifier =
                            modifier
                                .fillMaxWidth()
                                .padding(16.dp, 4.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .border(2.dp, ComponentStrokeColor, RoundedCornerShape(20.dp)),
                        value = password.value,
                        onValueChange = { viewModel.updatePassword(it) },
                        placeholder = { Text(stringResource(R.string.password)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Email",
                            )
                        },
                        visualTransformation = PasswordVisualTransformation(),
                    )

                    OutlinedTextField(
                        singleLine = true,
                        modifier =
                            modifier
                                .fillMaxWidth()
                                .padding(16.dp, 4.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .border(2.dp, ComponentStrokeColor, RoundedCornerShape(20.dp)),
                        value = confirmPassword.value,
                        onValueChange = { viewModel.updateConfirmPassword(it) },
                        placeholder = { Text(stringResource(R.string.confirm_password)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Email",
                            )
                        },
                        visualTransformation = PasswordVisualTransformation(),
                    )

                    errorMessage.value?.let { resId ->
                        Text(
                            text = stringResource(id = resId),
                            color = RedColor,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 4.dp),
                            textAlign = TextAlign.Center,
                        )
                    }

                    Spacer(modifier = Modifier.padding(4.dp))

                    Text(
                        text = stringResource(R.string.sign_up_info),
                        fontSize = 12.sp,
                        color = LightGreyColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 20.dp),
                    )

                    Spacer(Modifier.padding(8.dp))

                    Button(
                        onClick = { viewModel.onSignUpClick(navController) },
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = WhiteColor,
                            ),
                        modifier =
                            modifier
                                .fillMaxWidth()
                                .padding(16.dp, 0.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.sign_up),
                            fontSize = 16.sp,
                            color = ComponentBackgroundColor,
                            modifier = modifier.padding(0.dp, 6.dp),
                        )
                    }

                    Spacer(Modifier.padding(4.dp))

                    Text(
                        text = stringResource(R.string.or),
                        fontSize = 16.sp,
                        color = TextColor,
                    )

                    Spacer(Modifier.padding(4.dp))

                    AuthenticationButton(R.string.sign_up_with_google) { credential ->
                        viewModel.onSignUpWithGoogle(credential, navController)
                    }

                    Spacer(Modifier.padding(4.dp))

                    TextButton(
                        onClick = { navController.navigate("signIn") },
                        modifier = Modifier.padding(horizontal = 16.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.sign_in_description),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            color = TextColor,
                        )
                    }
                }
            }
        }
    }
}

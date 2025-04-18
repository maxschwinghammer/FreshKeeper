package com.freshkeeper.screens.authentication.signIn

import android.annotation.SuppressLint
import android.credentials.GetCredentialException
import android.util.Log
import android.widget.EditText
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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPasswordOption
import androidx.credentials.PendingGetCredentialRequest
import androidx.credentials.pendingGetCredentialRequest
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.freshkeeper.R
import com.freshkeeper.screens.authentication.viewmodel.SignInViewModel
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.FreshKeeperTheme
import com.freshkeeper.ui.theme.RedColor
import com.freshkeeper.ui.theme.TextColor
import com.freshkeeper.ui.theme.WhiteColor
import kotlinx.coroutines.launch

@SuppressLint("NewApi")
@Suppress("ktlint:standard:function-naming")
@Composable
fun EmailSignInScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val viewModel: SignInViewModel = hiltViewModel()

    val email = viewModel.email.collectAsState()
    val password = viewModel.password.collectAsState()
    val errorMessage = viewModel.errorMessage.collectAsState()
    val context = LocalContext.current
    val activity = context as FragmentActivity

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
                    Spacer(Modifier.padding(12.dp))
                    Text(
                        text = stringResource(R.string.login_header),
                        fontSize = 26.sp,
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
                        keyboardOptions =
                            KeyboardOptions.Default.copy(
                                keyboardType =
                                    KeyboardType.Email,
                            ),
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
                                contentDescription = "Password",
                            )
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions =
                            KeyboardOptions.Default.copy(
                                keyboardType =
                                    KeyboardType.Password,
                            ),
                    )
                    errorMessage.value?.let { resId ->
                        Text(
                            text = stringResource(id = resId),
                            color = RedColor,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp),
                            textAlign = TextAlign.Center,
                        )
                    }
                    TextButton(
                        onClick = { navController.navigate("forgotPassword/${email.value}") },
                        modifier = Modifier.padding(horizontal = 16.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.forgot_password),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            color = TextColor,
                        )
                    }
                    Spacer(Modifier.padding(2.dp))
                    Button(
                        onClick = { viewModel.onSignInClick(navController, context, activity) },
                        colors = ButtonDefaults.buttonColors(containerColor = WhiteColor),
                        modifier =
                            modifier
                                .fillMaxWidth()
                                .padding(16.dp, 0.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.sign_in),
                            fontSize = 16.sp,
                            color = ComponentBackgroundColor,
                            modifier = modifier.padding(0.dp, 6.dp),
                        )
                    }
                    Spacer(Modifier.padding(8.dp))
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

    @Composable
    fun setupAutofillForCredentials(
        usernameEditText: EditText,
        passwordEditText: EditText,
    ) {
        val getPasswordOption = GetPasswordOption()
        val context = LocalContext.current

        val getCredRequest =
            GetCredentialRequest(
                listOf(getPasswordOption),
            )

        val coroutineScope = rememberCoroutineScope()
        LaunchedEffect(Unit) {
            coroutineScope.launch {
                try {
                    val credentialManager = CredentialManager.create(context)
                    val result =
                        credentialManager.getCredential(
                            context = context,
                            request = getCredRequest,
                        )

                    usernameEditText.pendingGetCredentialRequest =
                        PendingGetCredentialRequest(
                            getCredRequest,
                        ) {
                        }

                    passwordEditText.pendingGetCredentialRequest =
                        PendingGetCredentialRequest(
                            getCredRequest,
                        ) {
                        }
                } catch (e: GetCredentialException) {
                    Log.e("CredentialError", e.message.orEmpty())
                }
            }
        }
    }
}

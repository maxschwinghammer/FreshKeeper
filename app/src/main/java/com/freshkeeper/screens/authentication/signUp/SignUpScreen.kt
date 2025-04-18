package com.freshkeeper.screens.authentication.signUp

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.freshkeeper.R
import com.freshkeeper.screens.authentication.AuthenticationButton
import com.freshkeeper.screens.authentication.viewmodel.GoogleViewModel
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.FreshKeeperTheme
import com.freshkeeper.ui.theme.TextColor
import com.freshkeeper.ui.theme.WhiteColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun SignUpScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val googleViewModel: GoogleViewModel = hiltViewModel()
    val context = LocalContext.current
    val activity = context as FragmentActivity

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
                                .scale(1.25f)
                                .fillMaxWidth()
                                .padding(16.dp, 4.dp),
                    )
                    Spacer(Modifier.padding(12.dp))
                    Text(
                        text = stringResource(R.string.sign_up_header),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextColor,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.padding(12.dp))
                    Button(
                        onClick = { navController.navigate("emailSignUp") },
                        colors = ButtonDefaults.buttonColors(containerColor = WhiteColor),
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp, 0.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email icon",
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                        Text(
                            text = stringResource(R.string.sign_up_with_email),
                            color = ComponentBackgroundColor,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(0.dp, 6.dp),
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
                        googleViewModel.onSignInWithGoogle(
                            credential,
                            navController,
                            activity,
                        )
                    }
                    Spacer(Modifier.padding(8.dp))
                    SignUpInfo()
                    Spacer(Modifier.padding(20.dp))
                    TextButton(
                        onClick = { navController.navigate("signIn") },
                        border = BorderStroke(1.dp, AccentTurquoiseColor),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.sign_up_description),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            color = TextColor,
                            modifier = Modifier.padding(vertical = 4.dp),
                        )
                    }
                }
            }
        }
    }
}

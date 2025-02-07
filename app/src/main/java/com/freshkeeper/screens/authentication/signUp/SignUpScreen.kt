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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
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
import com.freshkeeper.ui.theme.LightGreyColor
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
                            context,
                            activity,
                        )
                    }

                    Spacer(Modifier.padding(8.dp))

                    SignUpInfo()

                    Spacer(Modifier.padding(8.dp))

                    TextButton(
                        onClick = { navController.navigate("signIn") },
                        modifier = Modifier.padding(horizontal = 16.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.sign_up_description),
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

@Composable
fun signUpInfoText(): AnnotatedString =
    buildAnnotatedString {
        pushStyle(SpanStyle(color = WhiteColor, fontSize = 14.sp))
        append(stringResource(R.string.sign_up_info_1) + " ")

        pushStyle(SpanStyle(color = AccentTurquoiseColor))
        withLink(LinkAnnotation.Url(url = "https://github.com/maxschwinghammer/FreshKeeper/blob/master/terms-of-service.md")) {
            append(stringResource(R.string.terms_of_service))
        }

        pushStyle(SpanStyle(color = WhiteColor))
        append(" " + stringResource(R.string.sign_up_info_2) + " ")

        pushStyle(SpanStyle(color = AccentTurquoiseColor))
        withLink(LinkAnnotation.Url(url = "https://github.com/maxschwinghammer/FreshKeeper/blob/master/privacy-policy.md")) {
            append(stringResource(R.string.privacy_policy))
        }
    }

@Suppress("ktlint:standard:function-naming")
@Composable
fun SignUpInfo() {
    val annotatedText = signUpInfoText()

    Text(
        text = annotatedText,
        style =
            TextStyle(
                fontSize = 12.sp,
                color = LightGreyColor,
                textAlign = TextAlign.Center,
            ),
    )
}

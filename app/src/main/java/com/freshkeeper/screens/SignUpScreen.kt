package com.freshkeeper.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.freshkeeper.R
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.FreshKeeperTheme
import com.freshkeeper.ui.theme.LightGreyColor
import com.freshkeeper.ui.theme.TextColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun SignUpScreen(navController: NavHostController) {
    FreshKeeperTheme {
        Scaffold {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(it),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth(0.9f)
                            .clip(RoundedCornerShape(20.dp))
                            .border(1.dp, ComponentStrokeColor, RoundedCornerShape(20.dp))
                            .background(ComponentBackgroundColor),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "Create an account",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextColor,
                            textAlign = TextAlign.Center,
                        )

                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        ) {
                            Text(
                                text =
                                    "By continuing, you accept the terms and conditions and " +
                                        "privacy policy.",
                                fontSize = 14.sp,
                                color = LightGreyColor,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 8.dp),
                            )
                        }
                        Button(
                            colors =
                                ButtonDefaults.buttonColors
                                    (containerColor = ComponentBackgroundColor),
                            onClick = { navController.navigate("home") },
                            modifier =
                                Modifier
                                    .padding(top = 16.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .border(1.dp, ComponentStrokeColor, RoundedCornerShape(20.dp)),
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.google),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text("Sign up with Google", color = TextColor)
                        }
                    }
                }
            }
        }
    }
}

package com.freshkeeper.screens.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.freshkeeper.R
import com.freshkeeper.navigation.BottomNavigationBar
import com.freshkeeper.screens.notifications.NotificationsViewModel
import com.freshkeeper.ui.theme.BottomNavBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.FreshKeeperTheme
import com.freshkeeper.ui.theme.GreyColor
import com.freshkeeper.ui.theme.TextColor
import java.util.Locale

@Suppress("ktlint:standard:function-naming")
@Composable
fun SettingsScreen(
    navController: NavHostController,
    notificationsViewModel: NotificationsViewModel,
    onLocaleChange: (String) -> Unit,
) {
    var selectedLanguage by remember { mutableStateOf(Locale.getDefault().language) }
    val termsOfServiceUrl = "https://github.com/maxschwinghammer/FreshKeeper/blob/master/terms-of-service.md"
    val privacyPolicyUrl = "https://github.com/maxschwinghammer/FreshKeeper/blob/master/privacy-policy.md"
    val imprintUrl = "https://github.com/maxschwinghammer/FreshKeeper/blob/master/imprint.md"

    FreshKeeperTheme {
        Scaffold(
            bottomBar = {
                Box(
                    modifier =
                        Modifier
                            .background(BottomNavBackgroundColor)
                            .padding(horizontal = 10.dp),
                ) {
                    BottomNavigationBar(selectedIndex = 3, navController, notificationsViewModel)
                }
            },
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(it),
            ) {
                Text(
                    text = stringResource(R.string.settings),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextColor,
                    modifier = Modifier.padding(16.dp),
                )
                LazyColumn(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(top = 55.dp, start = 15.dp, end = 15.dp),
                    verticalArrangement = Arrangement.spacedBy(15.dp),
                ) {
                    item {
                        Button(
                            onClick = { navController.navigate("profileSettings") },
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = TextColor,
                                ),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, ComponentStrokeColor),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start,
                            ) {
                                Text(
                                    text = stringResource(R.string.profile_settings),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextColor,
                                    modifier =
                                        Modifier.padding(
                                            top = 10.dp,
                                            bottom = 10.dp,
                                            end = 10.dp,
                                        ),
                                )
                            }
                        }
                    }
                    item {
                        Button(
                            onClick = { navController.navigate("notificationSettings") },
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = TextColor,
                                ),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, ComponentStrokeColor),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start,
                            ) {
                                Text(
                                    text = stringResource(R.string.notification_settings),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextColor,
                                    modifier =
                                        Modifier.padding(
                                            top = 10.dp,
                                            bottom = 10.dp,
                                            end = 10.dp,
                                        ),
                                )
                            }
                        }
                    }
                    item {
                        LanguageDropdownMenu(
                            currentLanguage = selectedLanguage,
                            onLanguageSelected = { languageCode ->
                                selectedLanguage = languageCode
                                onLocaleChange(languageCode)
                            },
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                        )
                    }
                    item {
                        Column(
                            modifier =
                                Modifier
                                    .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp))
                                    .clip(RoundedCornerShape(10.dp))
                                    .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Text(
                                text = stringResource(R.string.legal_information),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextColor,
                            )
                            ExternalLinkButton(
                                url = termsOfServiceUrl,
                                label = stringResource(R.string.settings_terms_of_service),
                            )
                            ExternalLinkButton(
                                url = privacyPolicyUrl,
                                label = stringResource(R.string.settings_privacy_policy),
                            )
                            ExternalLinkButton(
                                url = imprintUrl,
                                label = stringResource(R.string.settings_imprint),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun ExternalLinkButton(
    url: String,
    label: String,
) {
    val context = LocalContext.current
    Button(
        onClick = {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        },
        colors =
            ButtonDefaults.buttonColors(
                containerColor = GreyColor,
                contentColor = TextColor,
            ),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, ComponentStrokeColor),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextColor,
                modifier = Modifier.padding(top = 10.dp, bottom = 10.dp, end = 10.dp),
            )
            Icon(
                painter = painterResource(R.drawable.right_arrow_short),
                contentDescription = "Icon",
                modifier =
                    Modifier
                        .size(16.dp)
                        .align(Alignment.CenterVertically),
            )
        }
    }
}

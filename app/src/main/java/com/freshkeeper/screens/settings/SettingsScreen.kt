package com.freshkeeper.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.freshkeeper.R
import com.freshkeeper.model.Membership
import com.freshkeeper.navigation.BottomNavigationBar
import com.freshkeeper.screens.LowerTransition
import com.freshkeeper.screens.UpperTransition
import com.freshkeeper.screens.notifications.viewmodel.NotificationsViewModel
import com.freshkeeper.screens.settings.viewmodel.SettingsViewModel
import com.freshkeeper.sheets.ManagePremiumSheet
import com.freshkeeper.ui.theme.BottomNavBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.FreshKeeperTheme
import com.freshkeeper.ui.theme.TextColor
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("ktlint:standard:function-naming")
@Composable
fun SettingsScreen(
    navController: NavHostController,
    onLocaleChange: (String) -> Unit,
) {
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val notificationsViewModel: NotificationsViewModel = hiltViewModel()

    var selectedLanguage by remember { mutableStateOf(Locale.getDefault().language) }
    val termsOfServiceUrl = "https://github.com/maxschwinghammer/FreshKeeper/blob/master/terms-of-service.md"
    val privacyPolicyUrl = "https://github.com/maxschwinghammer/FreshKeeper/blob/master/privacy-policy.md"
    val imprintUrl = "https://github.com/maxschwinghammer/FreshKeeper/blob/master/imprint.md"

    val membership by settingsViewModel.membership.collectAsState(initial = Membership())
    val managePremiumSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    val listState = rememberLazyListState()
    val showTransition by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0
        }
    }

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
                        SettingsButton(stringResource(R.string.profile_settings)) {
                            navController.navigate("profileSettings")
                        }
                    }
                    item {
                        SettingsButton(stringResource(R.string.household_settings)) {
                            navController.navigate("householdSettings")
                        }
                    }
                    item {
                        SettingsButton(stringResource(R.string.notification_settings)) {
                            navController.navigate("notificationSettings")
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
                    item { BuyACoffeeButton() }
                    item {
                        UpgradeToPremiumVersionButton(
                            membership = membership,
                            onManagePremiumClick = {
                                coroutineScope.launch { managePremiumSheetState.show() }
                            },
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
                                termsOfServiceUrl,
                                stringResource(R.string.settings_terms_of_service),
                            )
                            ExternalLinkButton(
                                privacyPolicyUrl,
                                stringResource(R.string.settings_privacy_policy),
                            )
                            ExternalLinkButton(
                                imprintUrl,
                                stringResource(R.string.settings_imprint),
                            )
                        }
                    }
                    item {
                        SettingsButton(stringResource(R.string.help_and_contact)) {
                            navController.navigate("help")
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
                if (showTransition) {
                    UpperTransition()
                    LowerTransition(
                        modifier = Modifier.align(Alignment.BottomCenter),
                    )
                }
            }
            if (managePremiumSheetState.isVisible) {
                ManagePremiumSheet(
                    managePremiumSheetState,
                    membership,
                    onCancelPremium = {},
                    onChangePlan = {},
                )
            }
        }
    }
}

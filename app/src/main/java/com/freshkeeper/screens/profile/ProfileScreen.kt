package com.freshkeeper.screens.profile

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.freshkeeper.R
import com.freshkeeper.model.ProfilePicture
import com.freshkeeper.navigation.BottomNavigationBar
import com.freshkeeper.screens.notifications.viewmodel.NotificationsViewModel
import com.freshkeeper.screens.profile.viewmodel.ProfileViewModel
import com.freshkeeper.screens.profileSettings.convertBase64ToBitmap
import com.freshkeeper.ui.theme.BottomNavBackgroundColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.FreshKeeperTheme
import com.freshkeeper.ui.theme.TextColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun ProfileScreen(
    navController: NavHostController,
    userId: String,
) {
    val notificationsViewModel: NotificationsViewModel = hiltViewModel()
    val profileViewModel: ProfileViewModel = hiltViewModel()

    profileViewModel.getUserProfile(userId)

    val user = profileViewModel.user.collectAsState().value
    val memberSinceDays = profileViewModel.memberSinceDays.collectAsState().value

    fun formatMemberSince(days: Long): String =
        when {
            days > 365 -> {
                val years = days / 365
                val remainingMonths = (days % 365) / 30
                if (remainingMonths > 0) {
                    "Member since $years ${if (years == 1L) "year" else "years"} and" +
                        " $remainingMonths ${if (remainingMonths == 1L) "month" else "months"}"
                } else {
                    "Member since $years ${if (years == 1L) "year" else "years"}"
                }
            }
            days > 30 -> {
                val months = days / 30
                "Member since $months ${if (months == 1L) "month" else "months"}"
            }
            days == 1L -> "Member since yesterday"
            days == 0L -> "Member since today"
            else -> "Member since $days days"
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
                    BottomNavigationBar(selectedIndex = 2, navController, notificationsViewModel)
                }
            },
        ) { it ->
            Text(
                text = stringResource(R.string.profile),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextColor,
                modifier = Modifier.padding(16.dp),
            )
            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(top = 45.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp),
            ) {
                item {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(it),
                    ) {
                        Column(
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            user?.let {
                                ProfileCard(
                                    name = it.displayName,
                                    memberSince = formatMemberSince(memberSinceDays),
                                    profilePicture =
                                        profileViewModel.profilePicture
                                            .collectAsState()
                                            .value,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun ProfileCard(
    name: String?,
    memberSince: String?,
    profilePicture: ProfilePicture?,
) {
    Card(
        modifier =
            Modifier
                .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp)),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(ComponentBackgroundColor)
                    .padding(16.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier =
                        Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(50))
                            .border(1.dp, ComponentStrokeColor, RoundedCornerShape(50)),
                ) {
                    profilePicture?.let {
                        when (it.type) {
                            "base64" -> {
                                val decodedImage =
                                    it.image?.let { it1 ->
                                        convertBase64ToBitmap(it1)
                                    }
                                if (decodedImage != null) {
                                    Image(
                                        bitmap = decodedImage.asImageBitmap(),
                                        contentDescription = "Profile Picture",
                                        modifier = Modifier.fillMaxSize(),
                                    )
                                }
                            }
                            "url" -> {
                                Image(
                                    painter = rememberAsyncImagePainter(it.image),
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier.fillMaxSize(),
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = name ?: "Unknown user",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextColor,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = memberSince ?: "Member since unknown",
                        fontSize = 14.sp,
                        color = TextColor,
                    )
                }
            }
        }
    }
}

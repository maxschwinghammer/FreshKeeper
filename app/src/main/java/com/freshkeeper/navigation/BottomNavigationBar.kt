package com.freshkeeper.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.freshkeeper.R
import com.freshkeeper.ui.theme.ActiveIndicatorColor
import com.freshkeeper.ui.theme.BottomNavBackgroundColor
import com.freshkeeper.ui.theme.BottomNavIconColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun BottomNavigationBar(
    selectedIndex: Int,
    navController: NavController,
) {
    val barItems =
        listOf(
            BarItem(
                title = "Home",
                selectedIcon = Icon.Vector(Icons.Filled.Home),
                unselectedIcon = Icon.Vector(Icons.Outlined.Home),
                route = "home",
                hasNews = false,
                badgeCount = null,
            ),
            BarItem(
                title = "Group",
                selectedIcon = Icon.Resource(painterResource(id = R.drawable.people_filled)),
                unselectedIcon = Icon.Resource(painterResource(id = R.drawable.people_outlined)),
                route = "group",
                hasNews = false,
                badgeCount = null,
            ),
            BarItem(
                title = "Profile",
                selectedIcon = Icon.Vector(Icons.Filled.Person),
                unselectedIcon = Icon.Vector(Icons.Outlined.Person),
                route = "profile",
                hasNews = false,
                badgeCount = null,
            ),
            BarItem(
                title = "Settings",
                selectedIcon = Icon.Vector(Icons.Filled.Settings),
                unselectedIcon = Icon.Vector(Icons.Outlined.Settings),
                route = "settings",
                hasNews = true,
                badgeCount = null,
            ),
            BarItem(
                title = "Updates",
                selectedIcon = Icon.Vector(Icons.Filled.Notifications),
                unselectedIcon = Icon.Vector(Icons.Outlined.Notifications),
                route = "notifications",
                hasNews = false,
                badgeCount = 12,
            ),
        )
    NavigationBar(
        containerColor = BottomNavBackgroundColor,
    ) {
        barItems.forEachIndexed { index, barItem ->
            NavigationBarItem(
                colors =
                    NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        unselectedIconColor = Color.Gray,
                        indicatorColor = ActiveIndicatorColor,
                    ),
                selected = selectedIndex == index,
                onClick = { navController.navigate(barItem.route) },
                label = { Text(text = barItem.title) },
                icon = {
                    BadgedBox(badge = {
                        if (barItem.badgeCount != null) {
                            Badge {
                                if (barItem.badgeCount > 99) {
                                    Text(text = "99+")
                                } else {
                                    Text(text = barItem.badgeCount.toString())
                                }
                            }
                        } else if (barItem.hasNews) {
                            Badge()
                        }
                    }) {
                        val icon = if (selectedIndex == index) barItem.selectedIcon else barItem.unselectedIcon
                        when (icon) {
                            is Icon.Vector -> {
                                Icon(
                                    imageVector = icon.imageVector,
                                    contentDescription = barItem.title,
                                    tint = BottomNavIconColor,
                                )
                            }
                            is Icon.Resource -> {
                                Image(
                                    painter = icon.painter,
                                    contentDescription = barItem.title,
                                    modifier = Modifier.size(24.dp),
                                )
                            }
                        }
                    }
                },
            )
        }
    }
}

sealed class Icon {
    data class Vector(
        val imageVector: ImageVector,
    ) : Icon()

    data class Resource(
        val painter: Painter,
    ) : Icon()
}

data class BarItem(
    var title: String,
    var selectedIcon: Icon,
    var unselectedIcon: Icon,
    val hasNews: Boolean,
    val badgeCount: Int? = null,
    var route: String,
)

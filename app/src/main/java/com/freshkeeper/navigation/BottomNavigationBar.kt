package com.freshkeeper.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.freshkeeper.ui.theme.TextColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun BottomNavigationBar(
    selectedIndex: Int,
    navController: NavController,
) {
    val inventoryIconFilled = painterResource(id = R.drawable.inventory_filled_2)
    val inventoryIconOutlined = painterResource(id = R.drawable.inventory_outlined)
    val householdIconFilled = painterResource(id = R.drawable.people_filled)
    val householdIconOutlined = painterResource(id = R.drawable.people_outlined)

    val barItems =
        remember {
            listOf(
                BarItem(
                    title = "Home",
                    selectedIcon = Icon.Vector(Icons.Filled.Home),
                    unselectedIcon = Icon.Vector(Icons.Outlined.Home),
                    route = "home",
                    hasNews = false,
                ),
                BarItem(
                    title = "Inventory",
                    selectedIcon = Icon.Resource(inventoryIconFilled),
                    unselectedIcon = Icon.Resource(inventoryIconOutlined),
                    route = "inventory",
                    hasNews = false,
                ),
                BarItem(
                    title = "Household",
                    selectedIcon = Icon.Resource(householdIconFilled),
                    unselectedIcon = Icon.Resource(householdIconOutlined),
                    route = "household",
                    hasNews = false,
                ),
                BarItem(
                    title = "Settings",
                    selectedIcon = Icon.Vector(Icons.Filled.Settings),
                    unselectedIcon = Icon.Vector(Icons.Outlined.Settings),
                    route = "settings",
                    hasNews = true,
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
        }

    NavigationBar(
        containerColor = BottomNavBackgroundColor,
        modifier = Modifier.height(65.dp),
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
                onClick = {
                    navController.navigate(barItem.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
//                label = { Text(text = barItem.title, color = TextColor) },
                icon = {
                    BadgedBox(badge = {
                        if (barItem.badgeCount != null) {
                            Badge(containerColor = Color.Red) {
                                Text(
                                    text = if (barItem.badgeCount > 99) "99+" else barItem.badgeCount.toString(),
                                    color = TextColor,
                                )
                            }
                        } else if (barItem.hasNews) {
                            Badge(containerColor = Color.Red)
                        }
                    }) {
                        when (val icon = if (selectedIndex == index) barItem.selectedIcon else barItem.unselectedIcon) {
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

data class BarItem(
    val title: String,
    val selectedIcon: Icon,
    val unselectedIcon: Icon,
    val hasNews: Boolean,
    val badgeCount: Int? = null,
    val route: String,
)

sealed class Icon {
    data class Vector(
        val imageVector: ImageVector,
    ) : Icon()

    data class Resource(
        val painter: Painter,
    ) : Icon()
}

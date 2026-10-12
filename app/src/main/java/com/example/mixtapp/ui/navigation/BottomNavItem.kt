package com.example.mixtapp.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.mixtapp.R

data class BottomNavItem(
    val filledIcon: ImageVector,
    val outlineIcon: ImageVector,
    val route: String,
    @StringRes val label: Int
)

val bottomNavItems = listOf(
    BottomNavItem(
        filledIcon = Icons.Filled.Home,
        outlineIcon = Icons.Outlined.Home,
        route = Screen.Home.route,
        label = R.string.nav_home
    ),
    BottomNavItem(
        filledIcon = Icons.Filled.Search,
        outlineIcon = Icons.Outlined.Search,
        route = Screen.Search.route,
        label = R.string.search_title
    ),
    BottomNavItem(
        filledIcon = Icons.Filled.Person,
        outlineIcon = Icons.Outlined.Person,
        route = Screen.Profile.route,
        label = R.string.nav_profile
    )
)

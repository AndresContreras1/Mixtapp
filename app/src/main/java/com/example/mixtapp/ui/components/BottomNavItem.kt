package com.example.mixtapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.mixtapp.navigation.Screen

data class BottomNavItem(
    val filledIcon: ImageVector,
    val outlineIcon: ImageVector,
    // Patron de la ruta, para saber si el item esta seleccionado
    val route: String,
    // Ruta concreta a la que navega, distinta cuando la ruta lleva un id
    val destination: String
)

val bottomNavItems = listOf(
    BottomNavItem(
        filledIcon = Icons.Filled.Home,
        outlineIcon = Icons.Outlined.Home,
        route = Screen.Home.route,
        destination = Screen.Home.route
    ),
    BottomNavItem(
        filledIcon = Icons.Filled.Search,
        outlineIcon = Icons.Outlined.Search,
        route = Screen.Search.route,
        destination = Screen.Search.route
    ),
    BottomNavItem(
        filledIcon = Icons.Filled.Add,
        outlineIcon = Icons.Outlined.Add,
        route = Screen.WriteReview.route,
        destination = Screen.WriteReview.createRoute(
            albumId = Screen.WriteReview.DEFAULT_ALBUM_ID
        )
    ),
    BottomNavItem(
        filledIcon = Icons.AutoMirrored.Filled.List,
        outlineIcon = Icons.AutoMirrored.Outlined.List,
        route = Screen.MyReviews.route,
        destination = Screen.MyReviews.route
    ),
    BottomNavItem(
        filledIcon = Icons.Filled.Person,
        outlineIcon = Icons.Outlined.Person,
        route = Screen.Profile.route,
        destination = Screen.Profile.route
    )
)

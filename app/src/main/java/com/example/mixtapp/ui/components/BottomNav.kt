package com.example.mixtapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mixtapp.navigation.Screen
import com.example.mixtapp.ui.theme.CircleWine
import com.example.mixtapp.ui.theme.PalePink
import com.example.mixtapp.ui.theme.PrimaryPink

data class BottomNavItem(
    val filledIcon: ImageVector,
    val outlineIcon: ImageVector,
    // Patron de la ruta, para saber si el item esta seleccionado
    val route: String,
    // Ruta concreta a la que navega, distinta cuando la ruta lleva un id
    val destination: String = route
)

val bottomNavItems = listOf(
    BottomNavItem(Icons.Filled.Home, Icons.Outlined.Home, Screen.Home.route),
    BottomNavItem(Icons.Filled.Search, Icons.Outlined.Search, Screen.Search.route),
    BottomNavItem(
        Icons.Filled.Add,
        Icons.Outlined.Add,
        Screen.WriteReview.route,
        Screen.WriteReview.createRoute(albumId = Screen.WriteReview.DEFAULT_ALBUM_ID)
    ),
    BottomNavItem(
        Icons.AutoMirrored.Filled.List,
        Icons.AutoMirrored.Outlined.List,
        Screen.MyReviews.route
    ),
    BottomNavItem(Icons.Filled.Person, Icons.Outlined.Person, Screen.Profile.route)
)

@Composable
fun BottomNav(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    Surface(
        color = Color.Black.copy(alpha = 0.9f),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .height(64.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomNavItems.forEach { item ->
                val isSelected = currentRoute == item.route
                val icon = if (isSelected) item.filledIcon else item.outlineIcon

                when (item.route) {
                    Screen.WriteReview.route -> Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(PrimaryPink)
                            .clickable { navController.navigate(item.destination) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = item.route,
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Screen.Profile.route -> Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CircleWine.copy(alpha = 0.5f))
                            .clickable { navController.navigate(item.destination) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = item.route,
                            tint = PrimaryPink,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    else -> Icon(
                        imageVector = icon,
                        contentDescription = item.route,
                        tint = PalePink.copy(alpha = 0.6f),
                        modifier = Modifier
                            .size(28.dp)
                            .clickable { navController.navigate(item.destination) }
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun BottomNavPreview() {
    BottomNav(navController = rememberNavController())
}

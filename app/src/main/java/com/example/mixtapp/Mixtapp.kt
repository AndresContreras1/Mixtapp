package com.example.mixtapp

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mixtapp.ui.navigation.AppNavigation
import com.example.mixtapp.ui.navigation.BottomNav
import com.example.mixtapp.ui.navigation.NavigationLogic
import com.example.mixtapp.ui.navigation.Screen
import com.example.mixtapp.ui.utils.WriteReviewFab

@Composable
fun Mixtapp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    val mostrarBarra = NavigationLogic.shouldShowBottomBar(currentRoute)

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (mostrarBarra) {
                BottomNav(navController = navController)
            }
        },
        floatingActionButton = {
            if (mostrarBarra) {
                // El + abre la lista de albumes para elegir cual resenar
                WriteReviewFab(
                    onClick = { navController.navigate(Screen.SelectAlbum.route) }
                )
            }
        }
    ) {
        AppNavigation(
            navController = navController,
            modifier = Modifier.padding(it)
        )
    }
}

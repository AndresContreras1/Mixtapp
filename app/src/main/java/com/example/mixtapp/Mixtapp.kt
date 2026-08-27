package com.example.mixtapp

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mixtapp.navigation.AppNavigation
import com.example.mixtapp.navigation.NavigationLogic
import com.example.mixtapp.ui.components.BottomNav

@Composable
fun Mixtapp() {
    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    Scaffold(
        bottomBar = {
            if (NavigationLogic.shouldShowBottomBar(currentRoute)) {
                BottomNav(navController = navController)
            }
        }
    ) {
        AppNavigation(
            navController = navController,
            modifier = Modifier.padding(it)
        )
    }
}

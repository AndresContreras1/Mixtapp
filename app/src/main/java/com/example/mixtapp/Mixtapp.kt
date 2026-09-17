package com.example.mixtapp

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mixtapp.navigation.AppNavigation
import com.example.mixtapp.navigation.NavigationLogic
import com.example.mixtapp.navigation.Screen
import com.example.mixtapp.ui.components.BottomNav
import com.example.mixtapp.ui.components.WriteReviewFab

@Composable
fun Mixtapp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    val mixtappViewModel: MixtappViewModel = hiltViewModel()
    val state by mixtappViewModel.uiState.collectAsState()

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
                WriteReviewFab(
                    onClick = {
                        navController.navigate(
                            Screen.WriteReview.createRoute(albumId = state.albumParaResenar)
                        )
                    }
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

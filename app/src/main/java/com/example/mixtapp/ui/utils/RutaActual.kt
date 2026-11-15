package com.example.mixtapp.ui.utils

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

// La ruta de la pantalla que se ve: la usan el Scaffold (para mostrar la barra y el +) y la barra
// de abajo (para marcar la pestana). Al cambiar de pantalla se vuelve a calcular sola
@Composable
fun rutaActual(navController: NavHostController): String? {
    val navBackStackEntry = navController.currentBackStackEntryAsState()

    return navBackStackEntry.value?.destination?.route
}

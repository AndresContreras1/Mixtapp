package com.example.mixtapp.ui.screens.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

// Pantalla sin contenido: su unica tarea es decidir a donde entrar.
// Quien navega sigue siendo la pantalla, con lambdas, no el ViewModel
@Composable
fun SplashScreen(
    splashViewModel: SplashViewModel,
    navigateToHome: () -> Unit,
    navigateToLogin: () -> Unit,
) {
    val state by splashViewModel.uiState.collectAsState()

    LaunchedEffect(state.verificando, state.hayUsuario) {
        if (!state.verificando) {
            if (state.hayUsuario) {
                navigateToHome()
            } else {
                navigateToLogin()
            }
        }
    }
}

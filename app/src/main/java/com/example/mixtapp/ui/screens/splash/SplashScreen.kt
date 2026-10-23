package com.example.mixtapp.ui.screens.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.mixtapp.ui.theme.MixtappTheme

// Pantalla sin contenido: su unica tarea es decidir a donde entrar.
// Quien navega sigue siendo la pantalla, con lambdas, no el ViewModel
@Composable
fun SplashScreen(
    splashViewModel: SplashViewModel,
    navigateToHome: () -> Unit,
    navigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
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

    SplashScreenContent(modifier = modifier)
}

// Como en clase, la pantalla no tiene contenido: solo decide a donde entrar
@Composable
fun SplashScreenContent(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize())
}

@Composable
@Preview(showBackground = true)
fun SplashScreenPreview() {
    MixtappTheme(darkTheme = true) {
        SplashScreenContent()
    }
}

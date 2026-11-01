package com.example.mixtapp.ui.screens.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppBackground
import com.example.mixtapp.ui.utils.LogoText

// Su unica tarea es decidir a donde entrar. Quien navega sigue siendo la pantalla, con lambdas,
// no el ViewModel
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

// Mientras se decide a donde entrar se ve el fondo de la app con el logo en el centro
@Composable
fun SplashScreenContent(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        AppBackground()

        LogoText(
            fontSize = 56.sp,
            fontWeight = FontWeight.Black,
            lineHeight = 60.sp,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
@Preview(showBackground = true, widthDp = 393, heightDp = 852)
fun SplashScreenPreview() {
    MixtappTheme(darkTheme = true) {
        SplashScreenContent()
    }
}

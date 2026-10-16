package com.example.mixtapp.ui.screens.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.mixtapp.ui.utils.AppBackground
import com.example.mixtapp.ui.utils.LogoText

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
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val progress by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(700, easing = FastOutSlowInEasing),
        label = "splashLogo"
    )

    Box(modifier = modifier.fillMaxSize()) {
        AppBackground()
        LogoText(
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 52.sp,
            modifier = Modifier
                .align(Alignment.Center)
                .graphicsLayer {
                    alpha = progress
                    scaleX = 0.92f + 0.08f * progress
                    scaleY = 0.92f + 0.08f * progress
                }
        )
    }

    LaunchedEffect(state.verificando, state.hayUsuario) {
        if (!state.verificando) {
            if (state.hayUsuario) {
                navigateToHome()
            } else {
                navigateToLogin()
            }
        }
    }

    Box(modifier = modifier.fillMaxSize())
}

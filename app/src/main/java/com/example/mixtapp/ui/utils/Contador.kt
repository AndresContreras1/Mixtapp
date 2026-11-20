package com.example.mixtapp.ui.utils

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import kotlin.math.roundToInt

// Cuanto tarda el numero en llegar a su valor
private const val CUENTA_MS = 1200

// Un numero que cuenta desde 0 hasta su valor la primera vez que se muestra, como en los resumenes
// del ano: 0, 1, 2... 42 resenas. Devuelve el numero que toca pintar ahora
@Composable
fun numeroQueCuenta(valor: Int): Int {
    val cuenta = remember { Animatable(0f) }
    LaunchedEffect(valor) {
        cuenta.animateTo(valor.toFloat(), tween(CUENTA_MS, easing = FastOutSlowInEasing))
    }
    return cuenta.value.roundToInt()
}

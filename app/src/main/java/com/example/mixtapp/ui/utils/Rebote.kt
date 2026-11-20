package com.example.mixtapp.ui.utils

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput

// Que tanto se encoge lo que se toca
private const val ESCALA_AL_TOCAR = 0.94f

// La animacion de todo lo que se toca: mientras el dedo esta encima se encoge un poco y al soltar
// vuelve con un rebote, asi se siente que respondio. Escucha el toque antes que los hijos (pase
// Initial) y no lo consume: el clic del boton o de la tarjeta sigue funcionando igual
fun Modifier.rebote(): Modifier = composed {
    var presionado by remember { mutableStateOf(false) }
    val escala by animateFloatAsState(
        targetValue = if (presionado) ESCALA_AL_TOCAR else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "rebote",
    )

    this
        .graphicsLayer {
            scaleX = escala
            scaleY = escala
        }
        .pointerInput(Unit) {
            awaitEachGesture {
                awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                presionado = true
                waitForUpOrCancellation(pass = PointerEventPass.Initial)
                presionado = false
            }
        }
}

package com.example.mixtapp.ui.utils

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

// Solo las primeras tarjetas entran escalonadas; las demas ya llegan con el scroll
private const val ESCALONADAS = 8
private const val RETRASO_ENTRE_TARJETAS_MS = 55
private const val DURACION_MS = 380

// Las tarjetas de una lista entran una tras otra: cada una aparece y sube un poco despues de la
// anterior. Solo pasa la primera vez: si la tarjeta sale con el scroll y vuelve, ya esta en su lugar
// (rememberSaveable guarda que ya aparecio mientras la lista exista)
@Composable
fun Modifier.aparecer(indice: Int): Modifier {
    var yaAparecio by rememberSaveable { mutableStateOf(indice >= ESCALONADAS) }
    val avance = remember { Animatable(if (yaAparecio) 1f else 0f) }

    LaunchedEffect(Unit) {
        if (!yaAparecio) {
            delay((indice * RETRASO_ENTRE_TARJETAS_MS).toLong())
            avance.animateTo(1f, tween(DURACION_MS, easing = FastOutSlowInEasing))
            yaAparecio = true
        }
    }

    return this.graphicsLayer {
        alpha = avance.value
        translationY = (1f - avance.value) * 24.dp.toPx()
    }
}

// Lo mismo para un bloque entero de una lista (una seccion del inicio): lo envuelve en un Box que
// entra escalonado
@Composable
fun Aparece(indice: Int, contenido: @Composable () -> Unit) {
    Box(modifier = Modifier.aparecer(indice)) {
        contenido()
    }
}

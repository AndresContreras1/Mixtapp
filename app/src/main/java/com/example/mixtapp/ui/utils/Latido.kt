package com.example.mixtapp.ui.utils

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// Cuanto crece lo que late antes de volver con el resorte
private const val ESCALA_DEL_LATIDO = 1.35f

// Cuantas particulas salen en el estallido y cuanto duran
private const val PARTICULAS = 6
private const val DURACION_DEL_ESTALLIDO_MS = 520

// Lo que se activa (el corazon, la estrella, el marcador) late: crece y vuelve con un resorte. Solo
// late cuando pasa de inactivo a activo; si llega activo desde el principio (el item ya tenia like)
// se queda quieto. retrasoMs deja que varias cosas latan una tras otra, como las estrellas
@Composable
fun Modifier.latido(activo: Boolean, retrasoMs: Int = 0): Modifier {
    val escala = remember { Animatable(1f) }
    var anterior by remember { mutableStateOf(activo) }

    LaunchedEffect(activo) {
        if (activo && !anterior) {
            delay(retrasoMs.toLong())
            escala.animateTo(ESCALA_DEL_LATIDO, tween(durationMillis = 110, easing = FastOutSlowInEasing))
            escala.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow))
        }
        anterior = activo
    }

    return this.graphicsLayer {
        scaleX = escala.value
        scaleY = escala.value
    }
}

// Cuando lo que lleva este modificador se activa, salen seis puntos del color hacia afuera y se
// apagan, como un pequeno estallido alrededor del corazon. Se dibuja encima, sin cambiar el tamano
@Composable
fun Modifier.estallido(activo: Boolean, color: Color): Modifier {
    val avance = remember { Animatable(1f) }
    var anterior by remember { mutableStateOf(activo) }

    LaunchedEffect(activo) {
        if (activo && !anterior) {
            avance.snapTo(0f)
            avance.animateTo(1f, tween(durationMillis = DURACION_DEL_ESTALLIDO_MS))
        }
        anterior = activo
    }

    return this.drawWithContent {
        drawContent()
        val p = avance.value
        if (p < 1f) {
            val centro = Offset(size.width / 2f, size.height / 2f)
            val distancia = size.minDimension * (0.55f + 0.65f * p)
            val radio = 3.dp.toPx() * (1f - p)
            repeat(PARTICULAS) { i ->
                val angulo = (2 * PI * i / PARTICULAS - PI / 2).toFloat()
                drawCircle(
                    color = color.copy(alpha = 1f - p),
                    radius = radio,
                    center = Offset(centro.x + cos(angulo) * distancia, centro.y + sin(angulo) * distancia),
                )
            }
        }
    }
}

package com.example.mixtapp.ui.utils

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.example.mixtapp.ui.theme.Chicle
import com.example.mixtapp.ui.theme.Coral
import com.example.mixtapp.ui.theme.Magenta
import com.example.mixtapp.ui.theme.Violeta
import kotlin.math.sin
import kotlin.random.Random

// Cuantos papelitos caen y cuanto dura la lluvia
private const val PAPELITOS = 60
private const val DURACION_DEL_CONFETI_MS = 2200

// Los colores del confeti: los de la marca y un amarillo de fiesta
private val coloresDelConfeti = listOf(Chicle, Magenta, Violeta, Coral, Color(0xFFFFC857))

// Una lluvia de confeti sobre la pantalla cuando pasa algo para celebrar: votar en un duelo o llegar
// al final de Tu ano. Cae una vez cada vez que activo pasa de false a true. clave reinicia la cuenta
// (por ejemplo, la pestana del duelo): al cambiarla no se celebra lo que ya estaba. No recibe toques:
// la pantalla de abajo se sigue usando
@Composable
fun Confeti(
    activo: Boolean,
    clave: Any,
    modifier: Modifier = Modifier,
) {
    val avance = remember { Animatable(1f) }
    var anterior by remember(clave) { mutableStateOf(activo) }
    // Cada lluvia cae distinta
    var semilla by remember { mutableStateOf(0) }

    LaunchedEffect(activo, clave) {
        if (activo && !anterior) {
            semilla++
            avance.snapTo(0f)
            avance.animateTo(1f, tween(DURACION_DEL_CONFETI_MS, easing = LinearEasing))
        }
        anterior = activo
    }

    if (avance.value < 1f) {
        val papelitos = remember(semilla) {
            val azar = Random(semilla)
            List(PAPELITOS) {
                Papelito(
                    x = azar.nextFloat(),
                    retraso = azar.nextFloat() * 0.35f,
                    velocidad = 0.8f + azar.nextFloat() * 0.6f,
                    vaiven = 0.02f + azar.nextFloat() * 0.05f,
                    giro = azar.nextFloat() * 720f - 360f,
                    color = coloresDelConfeti[azar.nextInt(coloresDelConfeti.size)],
                )
            }
        }

        Canvas(modifier = modifier.fillMaxSize()) {
            val ancho = 7.dp.toPx()
            val alto = 12.dp.toPx()
            papelitos.forEach { papelito ->
                // Cada papelito empieza un poco despues y cae a su velocidad, balanceandose
                val p = ((avance.value - papelito.retraso) / (1f - papelito.retraso)).coerceIn(0f, 1f)
                if (p > 0f) {
                    val y = -alto + p * papelito.velocidad * (size.height + alto * 2)
                    val x = papelito.x * size.width + sin(p * 12f) * papelito.vaiven * size.width
                    rotate(degrees = papelito.giro * p, pivot = Offset(x, y)) {
                        drawRect(
                            color = papelito.color.copy(alpha = 1f - p * p),
                            topLeft = Offset(x - ancho / 2f, y - alto / 2f),
                            size = Size(ancho, alto),
                        )
                    }
                }
            }
        }
    }
}

// Un papelito: donde empieza, cuanto espera, que tan rapido cae, cuanto se balancea, cuanto gira y
// su color
private class Papelito(
    val x: Float,
    val retraso: Float,
    val velocidad: Float,
    val vaiven: Float,
    val giro: Float,
    val color: Color,
)

package com.example.mixtapp.ui.utils

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.mixtapp.ui.theme.Chicle
import com.example.mixtapp.ui.theme.MixtappTheme

// Cuanto tarda cada barra en subir: distintas para que no se muevan juntas
private val DURACIONES_MS = listOf(420, 560, 380, 500)

// Las barras del ecualizador que suben y bajan mientras suena un fragmento. Es solo decoracion: no
// sigue el audio, cada barra va a su propio ritmo
@Composable
fun Ecualizador(
    color: Color,
    tamano: Dp,
    modifier: Modifier = Modifier,
) {
    val transicion = rememberInfiniteTransition(label = "ecualizador")
    val alturas = DURACIONES_MS.mapIndexed { indice, duracion ->
        transicion.animateFloat(
            initialValue = 0.25f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = duracion, delayMillis = indice * 70, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "barra$indice",
        )
    }

    Canvas(modifier = modifier.size(tamano)) {
        val barras = alturas.size
        val hueco = size.width * 0.12f
        val ancho = (size.width - hueco * (barras - 1)) / barras
        alturas.forEachIndexed { indice, altura ->
            val alto = size.height * altura.value
            drawRoundRect(
                color = color,
                topLeft = Offset(x = indice * (ancho + hueco), y = size.height - alto),
                size = Size(ancho, alto),
                cornerRadius = CornerRadius(ancho / 2f, ancho / 2f),
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun EcualizadorPreview() {
    MixtappTheme(darkTheme = true) {
        Ecualizador(color = Chicle, tamano = 24.dp)
    }
}

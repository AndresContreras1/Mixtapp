package com.example.mixtapp.ui.utils

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.ui.theme.MixtappTheme

// Cuantas tarjetas fantasma se muestran y cuanto tarda el brillo en cruzar la pantalla
private const val TARJETAS_FANTASMA = 6
private const val CRUCE_DEL_BRILLO_MS = 1100

// La carga de una pantalla completa: en vez del circulo que gira, la forma de lo que va a llegar
// (tarjetas con su portada y dos lineas) con un brillo que las cruza de izquierda a derecha. Asi la
// pantalla no se ve vacia mientras llegan los datos
@Composable
fun LoadingBox(modifier: Modifier = Modifier) {
    val brillo = brilloDeCarga()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp)
            .testTag("cargando"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        repeat(TARJETAS_FANTASMA) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(brillo)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(14.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .background(brillo)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.45f)
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(brillo)
                    )
                }
            }
        }
    }
}

// El pincel del brillo: la superficie de las tarjetas con una franja mas clara que se mueve de
// izquierda a derecha. Todas las formas usan el mismo pincel
@Composable
fun brilloDeCarga(): Brush {
    val ancho = with(LocalDensity.current) { LocalConfiguration.current.screenWidthDp.dp.toPx() }
    val transicion = rememberInfiniteTransition(label = "brilloDeCarga")
    val x by transicion.animateFloat(
        initialValue = -ancho,
        targetValue = ancho * 2f,
        animationSpec = infiniteRepeatable(tween(CRUCE_DEL_BRILLO_MS, easing = LinearEasing), RepeatMode.Restart),
        label = "posicionDelBrillo",
    )
    val base = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.55f)
    val claro = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.95f)
    return Brush.linearGradient(
        colors = listOf(base, claro, base),
        start = Offset(x, 0f),
        end = Offset(x + ancho * 0.6f, 0f),
    )
}

@Composable
@Preview(showBackground = true)
fun LoadingBoxPreview() {
    MixtappTheme(darkTheme = true) {
        LoadingBox()
    }
}

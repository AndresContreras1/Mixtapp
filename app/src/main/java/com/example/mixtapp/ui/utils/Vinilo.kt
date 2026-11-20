package com.example.mixtapp.ui.utils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.mixtapp.ui.theme.MixtappTheme

// Una vuelta del disco: lento, como un tocadiscos
private const val VUELTA_MS = 2400

// Cuantos surcos se dibujan en el disco
private const val SURCOS = 6

// El vinilo que aparece sobre la esquina de la portada mientras suena el fragmento y gira como un
// disco: negro con sus surcos y en el centro la misma portada, recortada en circulo. Cuando la
// musica para, se encoge y desaparece
@Composable
fun ViniloGirando(
    imagenUrl: String,
    titulo: String,
    girando: Boolean,
    tamano: Dp,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = girando,
        modifier = modifier,
        enter = scaleIn(initialScale = 0.4f) + fadeIn(),
        exit = scaleOut(targetScale = 0.4f) + fadeOut(),
    ) {
        val transicion = rememberInfiniteTransition(label = "vinilo")
        val angulo by transicion.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(tween(VUELTA_MS, easing = LinearEasing), RepeatMode.Restart),
            label = "anguloDelVinilo",
        )

        Box(
            modifier = Modifier
                .size(tamano)
                .shadow(elevation = 10.dp, shape = CircleShape)
                .graphicsLayer { rotationZ = angulo },
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val radio = size.minDimension / 2f
                drawCircle(color = Color(0xFF121014), radius = radio)
                repeat(SURCOS) { surco ->
                    drawCircle(
                        color = Color.White.copy(alpha = 0.07f),
                        radius = radio * (0.5f + 0.08f * surco),
                        style = Stroke(width = 1.dp.toPx()),
                    )
                }
                // El brillo de la luz sobre el disco: se ve girar con el
                drawArc(
                    color = Color.White.copy(alpha = 0.12f),
                    startAngle = -60f,
                    sweepAngle = 40f,
                    useCenter = false,
                    style = Stroke(width = radio * 0.35f),
                    topLeft = Offset(radio * 0.3f, radio * 0.3f),
                    size = Size(radio * 1.4f, radio * 1.4f),
                )
            }

            ItemAsyncImage(
                imagenUrl = imagenUrl,
                titulo = titulo,
                esquinas = tamano,
                modifier = Modifier
                    .size(tamano * 0.4f)
                    .clip(CircleShape),
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ViniloGirandoPreview() {
    MixtappTheme(darkTheme = true) {
        ViniloGirando(imagenUrl = "", titulo = "Meteora", girando = true, tamano = 96.dp)
    }
}

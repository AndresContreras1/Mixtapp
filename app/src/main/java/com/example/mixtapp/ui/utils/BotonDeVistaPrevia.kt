package com.example.mixtapp.ui.utils

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.model.EstadoDeReproduccion
import com.example.mixtapp.ui.theme.MixtappTheme

// El play redondo del fragmento de 30 segundos, en el rosado de lo importante. Sin estado muestra el
// play; mientras carga, el circulo; mientras suena, las barras del ecualizador moviendose (tocarlo lo
// detiene) y un anillo alrededor que se llena en los 30 segundos. El anillo cuenta el tiempo desde
// que empieza a sonar: los fragmentos de Deezer duran 30 s. El estado llega del ViewModel y el toque
// solo avisa
@Composable
fun BotonDeVistaPrevia(
    // null si este item no esta sonando
    estado: EstadoDeReproduccion?,
    titulo: String,
    tamano: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sonando = estado == EstadoDeReproduccion.Sonando
    val avance = remember { Animatable(0f) }
    LaunchedEffect(sonando) {
        if (sonando) {
            avance.snapTo(0f)
            avance.animateTo(1f, tween(durationMillis = DURACION_DEL_FRAGMENTO_MS, easing = LinearEasing))
        } else {
            avance.snapTo(0f)
        }
    }
    val colorDelAnillo = MaterialTheme.colorScheme.onPrimary
    val descripcionDetener = stringResource(R.string.detener_fragmento)

    FilledIconButton(
        onClick = onClick,
        modifier = modifier
            .rebote()
            .size(tamano)
            .drawWithContent {
                drawContent()
                if (sonando) {
                    val grosor = 2.5.dp.toPx()
                    drawArc(
                        color = colorDelAnillo.copy(alpha = 0.9f),
                        startAngle = -90f,
                        sweepAngle = 360f * avance.value,
                        useCenter = false,
                        topLeft = Offset(grosor * 1.5f, grosor * 1.5f),
                        size = Size(size.width - grosor * 3f, size.height - grosor * 3f),
                        style = Stroke(width = grosor, cap = StrokeCap.Round),
                    )
                }
            },
        shape = CircleShape,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
    ) {
        Box(contentAlignment = Alignment.Center) {
            when (estado) {
                EstadoDeReproduccion.Cargando -> CircularProgressIndicator(
                    modifier = Modifier.size(tamano * 0.45f),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp,
                )
                EstadoDeReproduccion.Sonando -> Ecualizador(
                    color = MaterialTheme.colorScheme.onPrimary,
                    tamano = tamano * 0.42f,
                    modifier = Modifier.semantics { contentDescription = descripcionDetener },
                )
                else -> Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = stringResource(R.string.escuchar_fragmento_de, titulo),
                    modifier = Modifier.size(tamano * 0.55f),
                )
            }
        }
    }
}

// Lo que dura un fragmento de Deezer
private const val DURACION_DEL_FRAGMENTO_MS = 30_000

@Composable
@Preview(showBackground = true)
fun BotonDeVistaPreviaPreview() {
    MixtappTheme(darkTheme = true) {
        BotonDeVistaPrevia(estado = null, titulo = "Meteora", tamano = 48.dp, onClick = {})
    }
}

@Composable
@Preview(showBackground = true)
fun BotonDeVistaPreviaSonandoPreview() {
    MixtappTheme(darkTheme = true) {
        BotonDeVistaPrevia(estado = EstadoDeReproduccion.Sonando, titulo = "Meteora", tamano = 48.dp, onClick = {})
    }
}

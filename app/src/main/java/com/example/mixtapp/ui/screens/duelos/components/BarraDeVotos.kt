package com.example.mixtapp.ui.screens.duelos.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.ui.theme.MixtappTheme

// Lo que tarda la barra en moverse cuando llega un voto
private const val ANIMACION_DE_LA_BARRA_MS = 700

// La barra de los dos porcentajes: cada lado ocupa con weight la parte de los votos que lleva. Cuando
// llega un voto nuevo por el listener, la barra se desliza hasta el porcentaje nuevo. Solo se muestra
// si ya hay votos: sin votos la pantalla invita a votar
@Composable
fun BarraDeVotos(
    porcentajeA: Int,
    modifier: Modifier = Modifier,
) {
    val ladoA by animateFloatAsState(
        targetValue = porcentajeA.toFloat(),
        animationSpec = tween(durationMillis = ANIMACION_DE_LA_BARRA_MS),
        label = "barraDeVotos",
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(16.dp)
            .clip(CircleShape),
    ) {
        // weight no acepta 0: con 0 % ese lado queda casi invisible
        Spacer(
            modifier = Modifier
                .weight(ladoA.coerceAtLeast(1f))
                .height(16.dp)
                .background(MaterialTheme.colorScheme.primary)
        )
        Spacer(
            modifier = Modifier
                .weight((100f - ladoA).coerceAtLeast(1f))
                .height(16.dp)
                .background(MaterialTheme.colorScheme.tertiary)
        )
    }
}

@Composable
@Preview(showBackground = true)
fun BarraDeVotosPreview() {
    MixtappTheme(darkTheme = true) {
        BarraDeVotos(porcentajeA = 64)
    }
}

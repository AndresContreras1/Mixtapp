package com.example.mixtapp.ui.screens.duelos.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.ui.theme.MixtappTheme

// La barra de los dos porcentajes: cada lado ocupa con weight la parte de los votos que lleva. Cuando
// llega un voto nuevo por el listener, la barra rebota hasta el porcentaje nuevo. Solo se muestra
// si ya hay votos: sin votos la pantalla invita a votar
@Composable
fun BarraDeVotos(
    porcentajeA: Int,
    modifier: Modifier = Modifier,
) {
    // Al aparecer arranca en la mitad y crece hasta su porcentaje; cada voto nuevo la mueve con un
    // resorte que se pasa un poco y vuelve
    val ladoA = remember { Animatable(50f) }
    LaunchedEffect(porcentajeA) {
        ladoA.animateTo(
            targetValue = porcentajeA.toFloat(),
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessVeryLow),
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(16.dp)
            .clip(CircleShape),
    ) {
        // weight no acepta 0: con 0 % ese lado queda casi invisible
        Spacer(
            modifier = Modifier
                .weight(ladoA.value.coerceIn(1f, 99f))
                .height(16.dp)
                .background(MaterialTheme.colorScheme.primary)
        )
        Spacer(
            modifier = Modifier
                .weight((100f - ladoA.value).coerceIn(1f, 99f))
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

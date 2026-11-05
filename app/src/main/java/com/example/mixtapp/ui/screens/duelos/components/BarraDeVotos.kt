package com.example.mixtapp.ui.screens.duelos.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.ui.theme.MixtappTheme

// La barra de los dos porcentajes: cada lado ocupa con weight la parte de los votos que lleva, asi
// que se mueve sola cuando llega un voto nuevo
@Composable
fun BarraDeVotos(
    porcentajeA: Int,
    porcentajeB: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(14.dp)
            .clip(CircleShape),
    ) {
        // weight no acepta 0: con 0 % ese lado queda casi invisible
        Spacer(
            modifier = Modifier
                .weight(porcentajeA.coerceAtLeast(1).toFloat())
                .height(14.dp)
                .background(MaterialTheme.colorScheme.primary)
        )
        Spacer(
            modifier = Modifier
                .weight(porcentajeB.coerceAtLeast(1).toFloat())
                .height(14.dp)
                .background(MaterialTheme.colorScheme.tertiary)
        )
    }
}

@Composable
@Preview(showBackground = true)
fun BarraDeVotosPreview() {
    MixtappTheme(darkTheme = true) {
        BarraDeVotos(porcentajeA = 64, porcentajeB = 36)
    }
}

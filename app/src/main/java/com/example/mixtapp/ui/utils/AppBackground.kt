package com.example.mixtapp.ui.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.ui.theme.MixtappTheme

// El fondo clasico de Mixtapp: el color de fondo con tres circulos de la paleta. Va detras de
// todas las pantallas, dentro de un Box con el contenido encima
@Composable
fun AppBackground(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        DecorativeCircle(
            size = 240.dp,
            color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.55f),
            modifier = Modifier.offset(x = (-114).dp, y = (-38).dp)
        )

        DecorativeCircle(
            size = 228.dp,
            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.22f),
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 32.dp)
        )

        DecorativeCircle(
            size = 275.dp,
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 112.dp, y = (-24).dp)
        )
    }
}

@Composable
@Preview(showBackground = true)
fun AppBackgroundPreview() {
    MixtappTheme(darkTheme = true) {
        AppBackground()
    }
}

@Composable
@Preview(showBackground = true)
fun AppBackgroundLightPreview() {
    MixtappTheme(darkTheme = false) {
        AppBackground()
    }
}

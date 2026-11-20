package com.example.mixtapp.ui.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.ui.theme.Magenta
import com.example.mixtapp.ui.theme.MixtappTheme

// El degradado decorativo con el color de la portada (lo saca Palette en PortadaConColor): va arriba,
// detras de la imagen, y se desvanece hacia abajo. Solo decora: los textos y el resto del fondo no
// cambian. Lo usan el detalle y los datos curiosos. Sin color todavia no pinta nada
@Composable
fun DegradadoDePortada(
    color: Color?,
    modifier: Modifier = Modifier,
) {
    if (color == null) return

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(520.dp)
            .background(Brush.verticalGradient(listOf(color.copy(alpha = 0.55f), Color.Transparent)))
    )
}

@Composable
@Preview(showBackground = true)
fun DegradadoDePortadaPreview() {
    MixtappTheme(darkTheme = true) {
        DegradadoDePortada(color = Magenta)
    }
}

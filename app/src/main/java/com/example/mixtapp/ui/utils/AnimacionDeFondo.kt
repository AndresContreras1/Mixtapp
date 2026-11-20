package com.example.mixtapp.ui.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

// La animacion corta (GIF o WebP animado) que va de fondo arriba del detalle de un artista, tenue y
// desvanecida hacia abajo para que el contenido se lea encima. Viene de un enlace publico guardado en
// el documento del artista; Coil la descarga y la anima. Sin enlace no pinta nada
@Composable
fun AnimacionDeFondo(
    url: String,
    modifier: Modifier = Modifier,
) {
    if (url.isEmpty()) return

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(460.dp)
            // El desvanecido se aplica como mascara sobre la imagen animada
            .graphicsLayer {
                alpha = 0.55f
                compositingStrategy = CompositingStrategy.Offscreen
            }
            .drawWithContent {
                drawContent()
                drawRect(
                    brush = Brush.verticalGradient(listOf(Color.Black, Color.Black, Color.Transparent)),
                    blendMode = BlendMode.DstIn,
                )
            },
    ) {
        AsyncImage(
            model = url,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

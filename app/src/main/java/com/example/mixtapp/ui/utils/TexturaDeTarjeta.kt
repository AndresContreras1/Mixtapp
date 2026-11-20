package com.example.mixtapp.ui.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.ui.theme.Black
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.theme.White
import kotlin.random.Random

// Cuantos puntos de grano lleva la tarjeta por cada 10.000 px
private const val GRANO_DE_LA_TARJETA = 10

// La superficie con textura de las tarjetas que informan (resenas, comentarios, notificaciones, lo
// que suena): una sombra suave que la separa del fondo, un degradado que va de mas claro arriba a mas
// oscuro abajo, un brillo fino en el borde de arriba y el mismo grano del fondo. Asi no se ven planas
@Composable
fun Modifier.texturaDeTarjeta(forma: Shape): Modifier {
    val colores = MaterialTheme.colorScheme
    val oscuro = colores.background.luminance() < 0.5f
    val arriba = colores.surfaceContainerHigh.copy(alpha = if (oscuro) 0.92f else 0.95f)
    val abajo = colores.surfaceContainer.copy(alpha = if (oscuro) 0.88f else 0.92f)
    val brillo = (if (oscuro) White else Black).copy(alpha = 0.07f)
    val grano = (if (oscuro) White else Black).copy(alpha = 0.04f)

    return this
        .shadow(elevation = 8.dp, shape = forma, clip = true, ambientColor = Black, spotColor = Black)
        .background(Brush.verticalGradient(listOf(arriba, abajo)), forma)
        .border(width = 1.dp, brush = Brush.verticalGradient(listOf(brillo, brillo.copy(alpha = 0f))), shape = forma)
        // El grano depende del tamano: se arma una vez por tamano
        .drawWithCache {
            val azar = Random(seed = 11)
            val puntos = List(((size.width * size.height) / 10_000f * GRANO_DE_LA_TARJETA).toInt()) {
                Offset(x = azar.nextFloat() * size.width, y = azar.nextFloat() * size.height)
            }
            onDrawBehind {
                drawPoints(points = puntos, pointMode = PointMode.Points, color = grano, strokeWidth = 2f)
            }
        }
}

@Composable
@Preview(showBackground = true)
fun TexturaDeTarjetaPreview() {
    MixtappTheme(darkTheme = true) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .size(width = 240.dp, height = 120.dp)
                .texturaDeTarjeta(RoundedCornerShape(24.dp))
        )
    }
}

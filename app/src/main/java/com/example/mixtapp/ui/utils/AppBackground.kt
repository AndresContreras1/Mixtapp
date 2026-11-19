package com.example.mixtapp.ui.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.tooling.preview.Preview
import com.example.mixtapp.ui.theme.Black
import com.example.mixtapp.ui.theme.Crimson300
import com.example.mixtapp.ui.theme.Crimson400
import com.example.mixtapp.ui.theme.Crimson500
import com.example.mixtapp.ui.theme.Mauve400
import com.example.mixtapp.ui.theme.Mauve500
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.theme.Plum300
import com.example.mixtapp.ui.theme.Plum400
import com.example.mixtapp.ui.theme.Plum500
import com.example.mixtapp.ui.theme.White
import kotlin.random.Random

// El tono del fondo de cada seccion, para que se sientan distintas sin cambiar nada mas: cada uno
// tiene el color del resplandor de arriba y el de abajo, y que tan fuerte se ve
enum class TonoDeFondo(
    val arriba: Color,
    val abajo: Color,
    val intensidad: Float,
) {
    // Inicio: el rosado de la marca
    Inicio(arriba = Crimson400, abajo = Plum500, intensidad = 0.55f),

    // Buscar: ciruela, mas frio
    Buscar(arriba = Plum400, abajo = Mauve500, intensidad = 0.5f),

    // Duelos: rojo intenso, la seccion que mas llama la atencion
    Duelos(arriba = Crimson300, abajo = Crimson500, intensidad = 0.75f),

    // Perfil y Tu ano: malva
    Perfil(arriba = Mauve400, abajo = Plum500, intensidad = 0.5f),

    // Lo de las personas: Siguiendo, notificaciones, discusiones y otros perfiles
    Social(arriba = Plum500, abajo = Crimson500, intensidad = 0.5f),

    // Un item y lo que se hace con el: detalle, datos curiosos, resenas y mixtapes
    Item(arriba = Mauve400, abajo = Plum500, intensidad = 0.45f),

    // Tu ano en Mixtapp: los colores mas vivos
    Resumen(arriba = Crimson300, abajo = Plum300, intensidad = 0.65f),

    // Entrar y registrarse: sobrio, solo un resplandor suave arriba para no competir con los campos
    Cuenta(arriba = Crimson500, abajo = Color.Transparent, intensidad = 0.45f),
}

// Cuantos puntos lleva el grano por cada 10.000 px de pantalla
private const val PUNTOS_DEL_GRANO = 6

// El fondo de Mixtapp: el color de fondo con dos resplandores difuminados (arriba a la izquierda y
// abajo a la derecha) del tono de la seccion y un grano muy fino que le da textura. Los
// resplandores se desvanecen hacia el centro, asi no pasan detras de los textos ni de las tarjetas.
// Va detras de todas las pantallas, dentro de un Box con el contenido encima
@Composable
fun AppBackground(
    tono: TonoDeFondo,
    modifier: Modifier = Modifier,
) {
    // El modo sale del color de fondo del tema. En el claro el fondo es pastel: el mismo tono se ve
    // con menos fuerza y el grano es oscuro
    val fondo = MaterialTheme.colorScheme.background
    val oscuro = fondo.luminance() < 0.5f
    val fuerza = if (oscuro) tono.intensidad else tono.intensidad * 0.45f
    val grano = if (oscuro) White.copy(alpha = 0.035f) else Black.copy(alpha = 0.03f)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(fondo)
            // Los pinceles y los puntos dependen del tamano: se arman una vez por tamano y no en
            // cada dibujo
            .drawWithCache {
                val ancho = size.width
                val alto = size.height

                val resplandorArriba = Brush.radialGradient(
                    colors = listOf(tono.arriba.copy(alpha = fuerza), Color.Transparent),
                    center = Offset(x = ancho * 0.1f, y = 0f),
                    radius = ancho * 1.1f,
                )
                val resplandorAbajo = Brush.radialGradient(
                    colors = listOf(tono.abajo.copy(alpha = fuerza * 0.8f), Color.Transparent),
                    center = Offset(x = ancho, y = alto),
                    radius = ancho * 0.95f,
                )

                // La semilla fija deja el grano igual en cada dibujo
                val azar = Random(seed = 7)
                val puntos = List(((ancho * alto) / 10_000f * PUNTOS_DEL_GRANO).toInt()) {
                    Offset(x = azar.nextFloat() * ancho, y = azar.nextFloat() * alto)
                }

                onDrawBehind {
                    drawRect(brush = resplandorArriba)
                    drawRect(brush = resplandorAbajo)
                    drawPoints(points = puntos, pointMode = PointMode.Points, color = grano, strokeWidth = 2f)
                }
            }
    )
}

@Composable
@Preview(showBackground = true)
fun AppBackgroundPreview() {
    MixtappTheme(darkTheme = true) {
        AppBackground(tono = TonoDeFondo.Inicio)
    }
}

@Composable
@Preview(showBackground = true)
fun AppBackgroundDuelosPreview() {
    MixtappTheme(darkTheme = true) {
        AppBackground(tono = TonoDeFondo.Duelos)
    }
}

@Composable
@Preview(showBackground = true)
fun AppBackgroundLightPreview() {
    MixtappTheme(darkTheme = false) {
        AppBackground(tono = TonoDeFondo.Perfil)
    }
}

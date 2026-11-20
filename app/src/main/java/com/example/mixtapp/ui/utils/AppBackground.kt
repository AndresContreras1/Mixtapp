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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.tooling.preview.Preview
import com.example.mixtapp.ui.theme.Black
import com.example.mixtapp.ui.theme.Coral
import com.example.mixtapp.ui.theme.Crimson300
import com.example.mixtapp.ui.theme.Crimson400
import com.example.mixtapp.ui.theme.Magenta
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.theme.Plum300
import com.example.mixtapp.ui.theme.Plum400
import com.example.mixtapp.ui.theme.Violeta
import com.example.mixtapp.ui.theme.White
import kotlin.random.Random

// El tono del fondo de cada seccion, para que se sientan distintas sin cambiar nada mas: cada uno
// tiene el color del resplandor de arriba y el de abajo, y que tan fuerte se ve
enum class TonoDeFondo(
    val arriba: Color,
    val abajo: Color,
    val intensidad: Float,
) {
    // Inicio: magenta arriba y violeta abajo, el aire de fiesta de la marca
    Inicio(arriba = Magenta, abajo = Violeta, intensidad = 0.45f),

    // Buscar: violeta y ciruela, mas frio
    Buscar(arriba = Violeta, abajo = Plum400, intensidad = 0.45f),

    // Duelos: rojo y coral intensos, la seccion que mas llama la atencion
    Duelos(arriba = Crimson300, abajo = Coral, intensidad = 0.6f),

    // Perfil: magenta suave y ciruela
    Perfil(arriba = Plum300, abajo = Magenta, intensidad = 0.4f),

    // Lo de las personas: Siguiendo, notificaciones, discusiones y otros perfiles
    Social(arriba = Violeta, abajo = Crimson400, intensidad = 0.4f),

    // Un item y lo que se hace con el: detalle, datos curiosos, resenas y mixtapes
    Item(arriba = Plum400, abajo = Violeta, intensidad = 0.4f),

    // Tu ano en Mixtapp: los colores mas vivos
    Resumen(arriba = Coral, abajo = Magenta, intensidad = 0.65f),

    // Entrar y registrarse: sobrio, solo un resplandor suave arriba para no competir con los campos
    Cuenta(arriba = Magenta, abajo = Color.Transparent, intensidad = 0.3f),
}

// Cuantos puntos lleva el grano por cada 10.000 px de pantalla
private const val PUNTOS_DEL_GRANO = 6

// Cuantas burbujas lleva el fondo
private const val BURBUJAS = 8

// El fondo de Mixtapp: el color de fondo con dos resplandores difuminados (arriba a la izquierda y
// abajo a la derecha) del tono de la seccion, las burbujas de la marca y un grano muy fino. Las
// burbujas son circulos difuminados de varios tamanos, con un borde tenue que deja ver su forma: se
// notan como textura sin tapar el contenido. Cambian de lugar en cada seccion (la semilla es el tono).
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

                // Las burbujas: cada seccion las tiene en otro lugar, pero siempre en el mismo
                val azarDeBurbujas = Random(seed = tono.ordinal + 3)
                val burbujas = List(BURBUJAS) { indice ->
                    val radio = ancho * (0.12f + azarDeBurbujas.nextFloat() * 0.26f)
                    val centro = Offset(x = azarDeBurbujas.nextFloat() * ancho, y = azarDeBurbujas.nextFloat() * alto)
                    val color = if (indice % 2 == 0) tono.arriba else tono.abajo
                    val alfa = fuerza * (0.28f + azarDeBurbujas.nextFloat() * 0.22f)
                    Burbuja(
                        centro = centro,
                        radio = radio,
                        relleno = Brush.radialGradient(
                            colors = listOf(color.copy(alpha = alfa), color.copy(alpha = alfa * 0.45f), Color.Transparent),
                            center = centro,
                            radius = radio,
                        ),
                        borde = color.copy(alpha = alfa * 0.5f),
                    )
                }

                onDrawBehind {
                    drawRect(brush = resplandorArriba)
                    drawRect(brush = resplandorAbajo)
                    burbujas.forEach { burbuja ->
                        drawCircle(brush = burbuja.relleno, radius = burbuja.radio, center = burbuja.centro)
                        drawCircle(
                            color = burbuja.borde,
                            radius = burbuja.radio * 0.9f,
                            center = burbuja.centro,
                            style = Stroke(width = 1.5f),
                        )
                    }
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

// Una burbuja del fondo: donde va, su tamano, el relleno difuminado y el borde tenue
private class Burbuja(
    val centro: Offset,
    val radio: Float,
    val relleno: Brush,
    val borde: Color,
)

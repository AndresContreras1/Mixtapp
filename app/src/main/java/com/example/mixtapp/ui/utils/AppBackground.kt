package com.example.mixtapp.ui.utils

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// El tono del fondo de cada seccion, para que se sientan distintas sin cambiar nada mas: cada uno
// tiene el color del resplandor de arriba y el de abajo, y que tan fuerte se ve
enum class TonoDeFondo(
    val arriba: Color,
    val abajo: Color,
    val intensidad: Float,
    // Las burbujas con el borde definido y menos difuminadas: la imagen con la que empezo la app
    val burbujasMarcadas: Boolean = false,
) {
    // Inicio: magenta arriba y violeta abajo, el aire de fiesta de la marca, con las burbujas marcadas
    Inicio(arriba = Magenta, abajo = Violeta, intensidad = 0.45f, burbujasMarcadas = true),

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

// Cuanto tarda el fondo en completar su movimiento y volver al punto de partida
private const val CICLO_DEL_FONDO_MS = 24_000

// El fondo de Mixtapp: el color de fondo con dos resplandores difuminados (arriba a la izquierda y
// abajo a la derecha) del tono de la seccion, las burbujas de la marca y un grano muy fino. Las
// burbujas son circulos de varios tamanos con un borde que deja ver su forma: se notan como textura
// sin tapar el contenido. Cambian de lugar en cada seccion (la semilla es el tono). El fondo respira:
// las burbujas flotan despacio alrededor de su lugar y los resplandores laten suave. Va detras de
// todas las pantallas, dentro de un Box con el contenido encima
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

    // El tiempo del movimiento, de 0 a 1. Cada burbuja da vueltas completas en ese tiempo, asi el
    // final empalma con el principio. Se lee solo al dibujar: el fondo no se recompone
    val transicion = rememberInfiniteTransition(label = "fondo")
    val tiempo = transicion.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(CICLO_DEL_FONDO_MS, easing = LinearEasing), RepeatMode.Restart),
        label = "tiempoDelFondo",
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(fondo)
            // Los resplandores y las burbujas se mueven: se dibujan en cada cuadro, pero sus
            // pinceles se arman una vez por tamano
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

                // Las burbujas: cada seccion las tiene en otro lugar, pero siempre en el mismo. El
                // pincel se arma alrededor del origen y al dibujar se mueve a su lugar
                val azarDeBurbujas = Random(seed = tono.ordinal + 3)
                val burbujas = List(BURBUJAS) { indice ->
                    val radio = ancho * (0.12f + azarDeBurbujas.nextFloat() * 0.26f)
                    val centro = Offset(x = azarDeBurbujas.nextFloat() * ancho, y = azarDeBurbujas.nextFloat() * alto)
                    val color = if (indice % 2 == 0) tono.arriba else tono.abajo
                    val alfa = fuerza * (0.28f + azarDeBurbujas.nextFloat() * 0.22f)
                    val marcadas = tono.burbujasMarcadas
                    // Marcadas: el relleno casi parejo y mas fuerte hacia el borde, como una pompa
                    val fuerte = (alfa * 1.5f).coerceAtMost(1f)
                    val relleno = if (marcadas) {
                        listOf(color.copy(alpha = fuerte * 0.45f), color.copy(alpha = fuerte * 0.55f), color.copy(alpha = fuerte * 0.8f))
                    } else {
                        listOf(color.copy(alpha = alfa), color.copy(alpha = alfa * 0.45f), Color.Transparent)
                    }
                    Burbuja(
                        centro = centro,
                        radio = radio,
                        relleno = Brush.radialGradient(colors = relleno, center = Offset.Zero, radius = radio),
                        borde = color.copy(alpha = if (marcadas) (alfa * 2.4f).coerceAtMost(1f) else alfa * 0.5f),
                        radioDelBorde = if (marcadas) radio else radio * 0.9f,
                        grosorDelBorde = if (marcadas) 2.5.dp.toPx() else 1.5f,
                        vueltas = 1 + indice % 2,
                        fase = azarDeBurbujas.nextFloat(),
                    )
                }

                onDrawBehind {
                    val t = tiempo.value
                    // Los resplandores laten: suben y bajan un poco su fuerza dos veces por ciclo
                    val latido = 0.88f + 0.12f * sin(2f * PI.toFloat() * t * 2f)
                    drawRect(brush = resplandorArriba, alpha = latido)
                    drawRect(brush = resplandorAbajo, alpha = latido)
                    burbujas.forEach { burbuja ->
                        // Cada burbuja flota en un pequeno ovalo alrededor de su lugar
                        val angulo = 2f * PI.toFloat() * (t * burbuja.vueltas + burbuja.fase)
                        val centro = burbuja.centro + Offset(
                            x = cos(angulo) * burbuja.radio * 0.18f,
                            y = sin(angulo) * burbuja.radio * 0.28f,
                        )
                        translate(left = centro.x, top = centro.y) {
                            drawCircle(brush = burbuja.relleno, radius = burbuja.radio, center = Offset.Zero)
                            drawCircle(
                                color = burbuja.borde,
                                radius = burbuja.radioDelBorde,
                                center = Offset.Zero,
                                style = Stroke(width = burbuja.grosorDelBorde),
                            )
                        }
                    }
                }
            }
    ) {
        // El grano no se mueve: va en su propia capa para no dibujarse en cada cuadro
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer()
                .drawWithCache {
                    // La semilla fija deja el grano igual en cada dibujo
                    val azar = Random(seed = 7)
                    val puntos = List(((size.width * size.height) / 10_000f * PUNTOS_DEL_GRANO).toInt()) {
                        Offset(x = azar.nextFloat() * size.width, y = azar.nextFloat() * size.height)
                    }
                    onDrawBehind {
                        drawPoints(points = puntos, pointMode = PointMode.Points, color = grano, strokeWidth = 2f)
                    }
                }
        )
    }
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

// Una burbuja del fondo: donde va, su tamano, el relleno, el borde y como flota (cuantas vueltas da
// en el ciclo y desde donde empieza)
private class Burbuja(
    val centro: Offset,
    val radio: Float,
    val relleno: Brush,
    val borde: Color,
    val radioDelBorde: Float,
    val grosorDelBorde: Float,
    val vueltas: Int,
    val fase: Float,
)

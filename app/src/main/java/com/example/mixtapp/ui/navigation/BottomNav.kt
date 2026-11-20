package com.example.mixtapp.ui.navigation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.rutaActual

// La barra de abajo: Inicio, Buscar, Duelos, Mapa y Perfil. Solo se ve en esas cinco rutas
// (NavigationLogic). Va en el tono mas oscuro y la pestana actual se distingue de tres formas: el
// icono lleno y un poco mas grande, la pastilla rosada detras y el texto rosado en negrita. La
// pastilla no aparece y desaparece: se desliza de una pestana a la otra. Cada icono avisa donde quedo
// su centro y la pastilla se dibuja detras de la barra, en el centro del icono de la pestana actual
@Composable
fun BottomNav(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val currentRoute = rutaActual(navController)

    val colores = MaterialTheme.colorScheme
    val densidad = LocalDensity.current

    // Donde quedo la barra y el centro de cada icono, para saber donde va la pastilla
    var origenDeLaBarra by remember { mutableStateOf(Offset.Zero) }
    val centros = remember { mutableStateMapOf<String, Offset>() }

    val destino = centros[currentRoute]
    // La primera vez la pastilla aparece en su lugar; despues se desliza con un resorte
    val centroX = remember { Animatable(Float.NaN) }
    LaunchedEffect(destino?.x) {
        val x = destino?.x ?: return@LaunchedEffect
        if (centroX.value.isNaN()) {
            centroX.snapTo(x)
        } else {
            centroX.animateTo(x, spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMediumLow))
        }
    }
    val centroY = destino?.y ?: 0f

    Box(
        modifier = modifier
            .onGloballyPositioned { origenDeLaBarra = it.positionInRoot() }
            .drawBehind {
                drawRect(color = colores.surfaceContainerLowest)
                // La pastilla solo se dibuja en las cinco pestanas
                if (destino != null && !centroX.value.isNaN()) {
                    val ancho = with(densidad) { ANCHO_DE_LA_PASTILLA.toPx() }
                    val alto = with(densidad) { ALTO_DE_LA_PASTILLA.toPx() }
                    drawRoundRect(
                        color = colores.primary.copy(alpha = 0.18f),
                        topLeft = Offset(centroX.value - ancho / 2f, centroY - alto / 2f),
                        size = Size(ancho, alto),
                        cornerRadius = CornerRadius(alto / 2f, alto / 2f),
                    )
                }
            },
    ) {
        NavigationBar(containerColor = Color.Transparent) {
            bottomNavItems.forEach { item ->
                val isSelected = currentRoute == item.route
                // El icono de la pestana actual crece un poco con un resorte
                val escala by animateFloatAsState(
                    targetValue = if (isSelected) 1.15f else 1f,
                    animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium),
                    label = "iconoDeLaPestana",
                )

                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = if (isSelected) item.filledIcon else item.outlineIcon,
                            // La ruta, como en clase
                            contentDescription = item.route,
                            // El centro se mide antes de agrandar el icono
                            modifier = Modifier
                                .onGloballyPositioned { icono ->
                                    val esquina = icono.positionInRoot() - origenDeLaBarra
                                    centros[item.route] = Offset(
                                        x = esquina.x + icono.size.width / 2f,
                                        y = esquina.y + icono.size.height / 2f,
                                    )
                                }
                                .graphicsLayer {
                                    scaleX = escala
                                    scaleY = escala
                                },
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(item.label),
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        )
                    },
                    selected = isSelected,
                    // La pastilla propia de NavigationBarItem va transparente: la dibuja la barra
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = colores.primary,
                        selectedTextColor = colores.primary,
                        indicatorColor = Color.Transparent,
                        unselectedIconColor = colores.onSurfaceVariant,
                        unselectedTextColor = colores.onSurfaceVariant,
                    ),
                    // Las pestanas no se apilan: se vuelve al inicio antes de abrir la otra, asi en la
                    // pila solo queda el inicio debajo y atras desde el inicio sale de la app. Al salir
                    // de una pestana su ViewModel se destruye y sus listeners se quitan
                    onClick = {
                        if (!isSelected) {
                            navController.navigate(item.route) {
                                popUpTo(Screen.Home.route) { inclusive = item.route == Screen.Home.route }
                            }
                        }
                    }
                )
            }
        }
    }
}

// El tamano de la pastilla de la pestana actual, el mismo de Material 3
private val ANCHO_DE_LA_PASTILLA = 64.dp
private val ALTO_DE_LA_PASTILLA = 32.dp

@Composable
@Preview(showBackground = true)
fun BottomNavPreview() {
    MixtappTheme(darkTheme = true) {
        BottomNav(navController = rememberNavController())
    }
}

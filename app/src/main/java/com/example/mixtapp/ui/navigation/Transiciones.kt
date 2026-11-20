package com.example.mixtapp.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.navigation.NavBackStackEntry

// Cuanto duran los cambios de pantalla: cortos, para que se sientan y no hagan esperar
private const val ENTRADA_MS = 320
private const val FUNDIDO_MS = 220

// Que tanto sube la pantalla nueva: una octava parte de su alto
private const val DESPLAZAMIENTO = 8

private val rutasDeLasPestanas = bottomNavItems.map { it.route }.toSet()

// Si se va de una pestana a otra de la barra de abajo
private fun AnimatedContentTransitionScope<NavBackStackEntry>.entrePestanas(): Boolean =
    initialState.destination.route in rutasDeLasPestanas && targetState.destination.route in rutasDeLasPestanas

// Las transiciones del NavHost. Entre pestanas solo hay un fundido corto; al abrir una pantalla
// (un detalle, un perfil, una playlist) la nueva sube un poco mientras aparece, y al volver baja y se
// desvanece. La de abajo se desvanece sin moverse
fun AnimatedContentTransitionScope<NavBackStackEntry>.entrada(): EnterTransition =
    if (entrePestanas()) {
        fadeIn(tween(FUNDIDO_MS))
    } else {
        slideInVertically(tween(ENTRADA_MS, easing = FastOutSlowInEasing)) { alto -> alto / DESPLAZAMIENTO } +
            fadeIn(tween(ENTRADA_MS))
    }

fun AnimatedContentTransitionScope<NavBackStackEntry>.salida(): ExitTransition =
    fadeOut(tween(FUNDIDO_MS))

fun AnimatedContentTransitionScope<NavBackStackEntry>.regreso(): EnterTransition =
    fadeIn(tween(FUNDIDO_MS))

fun AnimatedContentTransitionScope<NavBackStackEntry>.cierre(): ExitTransition =
    if (entrePestanas()) {
        fadeOut(tween(FUNDIDO_MS))
    } else {
        slideOutVertically(tween(ENTRADA_MS, easing = FastOutSlowInEasing)) { alto -> alto / DESPLAZAMIENTO } +
            fadeOut(tween(ENTRADA_MS))
    }

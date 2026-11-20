package com.example.mixtapp.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

// Una vibracion corta para confirmar lo que se toco: dar like, calificar, guardar y votar. Envuelve la
// accion y vibra antes de avisar al ViewModel
@Composable
fun conVibracion(accion: () -> Unit): () -> Unit {
    val vibracion = LocalHapticFeedback.current
    return {
        vibracion.performHapticFeedback(HapticFeedbackType.LongPress)
        accion()
    }
}

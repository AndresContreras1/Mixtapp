package com.example.mixtapp.ui.screens.review

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.ItemUi

// El item es nulable porque puede que el id no exista. Sirve igual para un album, una cancion o
// un artista
data class WriteReviewState(
    val item: ItemUi? = null,
    // Id de mi resena de este item: si existe, publicar la modifica en vez de crear otra
    val reviewId: String? = null,
    val rating: Int = 0,
    val reviewText: String = "",
    val listenedDate: String = "",
    // La base confirmo que la resena quedo guardada: la pantalla vuelve atras
    val navigate: Boolean = false,
    // Mientras se guarda no se manda otra resena
    val publicando: Boolean = false,
    // Arranca cargando porque lo primero que hace la pantalla es pedir el item
    val isLoading: Boolean = true,
    @StringRes val errorMessageRes: Int? = null,
)

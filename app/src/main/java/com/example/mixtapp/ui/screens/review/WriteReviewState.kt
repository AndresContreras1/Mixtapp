package com.example.mixtapp.ui.screens.review

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.Album

// El album es nulable porque puede que el id no exista
data class WriteReviewState(
    val album: Album? = null,
    // Id de mi resena de este album: si existe, publicar la modifica en vez de crear otra
    val reviewId: String? = null,
    val rating: Int = 0,
    val reviewText: String = "",
    val listenedDate: String = "",
    val publicada: Boolean = false,
    val publicando: Boolean = false,
    // Arranca cargando porque lo primero que hace la pantalla es pedir el album
    val isLoading: Boolean = true,
    @StringRes val errorMessageRes: Int? = null,
)

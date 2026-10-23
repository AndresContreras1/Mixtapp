package com.example.mixtapp.ui.screens.songreview

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.SongReviewUi

// El album es nulable porque puede que el id no exista
data class SongReviewsState(
    val song: SongReviewUi? = null,
    // Mi resena de este album, si ya la escribi
    val miResenaId: String? = null,
    // Mientras se borra no se manda otro DELETE
    val likesPendientes: Set<String> = emptySet(),
    val eliminando: Boolean = false,
    // Arranca cargando porque lo primero que hace la pantalla es pedir el album
    val isLoading: Boolean = true,
    @StringRes val errorMessageRes: Int? = null,
)

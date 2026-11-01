package com.example.mixtapp.ui.screens.following

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.FollowingUi
import com.example.mixtapp.data.model.ReviewUi

data class FollowingState(
    // Es nulable porque puede que todavia no hayan llegado los datos
    val following: FollowingUi? = null,
    // Las resenas que se ven: ya vienen filtradas por el nombre del autor
    val reviews: List<ReviewUi> = emptyList(),
    val friendQuery: String = "",
    // Arranca cargando porque el ViewModel empieza a escuchar al crearse
    val isLoading: Boolean = true,
    // No se pudo cargar la pestana: ocupa toda la pantalla
    @StringRes val errorMessageRes: Int? = null,
    // Fallo un like: la lista sigue visible y el error sale encima
    @StringRes val errorAccionRes: Int? = null,
)

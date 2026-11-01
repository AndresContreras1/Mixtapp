package com.example.mixtapp.ui.screens.myreviews

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.ReviewUi

data class MyReviewsState(
    // Mis resenas, la mas nueva primero: el repositorio ya las entrega en ese orden
    val reviews: List<ReviewUi> = emptyList(),
    // Arranca cargando porque el ViewModel empieza a escuchar mis resenas al crearse
    val isLoading: Boolean = true,
    // Mientras se borra no se manda otra eliminacion
    val eliminando: Boolean = false,
    // No se pudieron cargar mis resenas: si no hay ninguna, ocupa toda la pantalla
    @StringRes val errorMessageRes: Int? = null,
    // Fallo borrar o dar like: la lista sigue visible y el error sale encima
    @StringRes val errorAccionRes: Int? = null,
)

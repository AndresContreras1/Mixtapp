package com.example.mixtapp.ui.screens.userprofile

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.ItemResumenUi
import com.example.mixtapp.data.model.ReviewUi
import com.example.mixtapp.data.model.UsuarioUi

data class UserProfileState(
    val usuario: UsuarioUi? = null,
    val reviews: List<ReviewUi> = emptyList(),
    // Sus likes: albumes, canciones y artistas mezclados
    val favoritos: List<ItemResumenUi> = emptyList(),
    val isLoading: Boolean = true,
    val cargandoResenas: Boolean = false,
    @StringRes val errorMessageRes: Int? = null,
    // Cada seccion trae su propio error: si una falla, las demas se ven
    @StringRes val errorResenasRes: Int? = null,
    @StringRes val errorFavoritosRes: Int? = null,
    @StringRes val errorSeguimientoRes: Int? = null,
    @StringRes val errorLikeRes: Int? = null,
)

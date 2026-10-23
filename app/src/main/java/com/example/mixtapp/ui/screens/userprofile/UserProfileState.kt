package com.example.mixtapp.ui.screens.userprofile

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.MyReviewUi
import com.example.mixtapp.data.model.UsuarioUi

enum class TipoLista {
    SEGUIDORES, SEGUIDOS
}

data class UserProfileState(
    val usuario: UsuarioUi? = null,
    val reviews: List<MyReviewUi> = emptyList(),
    val seguidores: List<UsuarioUi> = emptyList(),
    val seguidos: List<UsuarioUi> = emptyList(),
    val tipoListaAbierta: TipoLista? = null,
    val isLoading: Boolean = true,
    val cargandoResenas: Boolean = false,
    val cambiandoSeguimiento: Boolean = false,
    @StringRes val errorMessageRes: Int? = null,
    @StringRes val errorResenasRes: Int? = null,
    @StringRes val errorSeguimientoRes: Int? = null,
)

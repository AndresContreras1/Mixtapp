package com.example.mixtapp.ui.screens.userprofile

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.MyReviewUi
import com.example.mixtapp.data.model.UsuarioUi

data class UserProfileState(
    val cambiandoSeguimiento: Boolean = false,
    @StringRes val errorSeguimientoRes: Int? = null,
    val usuario: UsuarioUi? = null,
    val reviews: List<MyReviewUi> = emptyList(),
    // Las resenas se piden en su propio launch, asi que tienen su propia carga
    val cargandoResenas: Boolean = true,
    // Arranca cargando porque lo primero que hace la pantalla es pedir el perfil
    val isLoading: Boolean = true,
    @StringRes val errorMessageRes: Int? = null,
    // El error de las resenas va aparte, porque se piden en otro launch
    @StringRes val errorResenasRes: Int? = null,
)

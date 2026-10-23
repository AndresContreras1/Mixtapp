package com.example.mixtapp.ui.screens.userprofile

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.MyReviewUi
import com.example.mixtapp.data.model.UsuarioUi

data class UserProfileState(
    val usuario: UsuarioUi? = null,
    val reviews: List<MyReviewUi> = emptyList(),
    val isLoading: Boolean = true,
    val cargandoResenas: Boolean = false,
    @StringRes val errorMessageRes: Int? = null,
    @StringRes val errorResenasRes: Int? = null,
    @StringRes val errorSeguimientoRes: Int? = null,
)

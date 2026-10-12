package com.example.mixtapp.ui.screens.userprofile

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.MyReviewUi
import com.example.mixtapp.data.model.UsuarioUi

data class UserProfileState(
    val usuario: UsuarioUi? = null,
    val reviews: List<MyReviewUi> = emptyList(),
    // Arranca cargando porque lo primero que hace la pantalla es pedir el perfil
    val isLoading: Boolean = true,
    @StringRes val errorMessageRes: Int? = null,
)

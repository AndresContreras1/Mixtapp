package com.example.mixtapp.ui.screens.crearmixtape

import androidx.annotation.StringRes

data class CrearMixtapeState(
    val nombre: String = "",
    val descripcion: String = "",
    // Mientras se guarda no se manda otra vez
    val guardando: Boolean = false,
    // Cuando queda creada, la pantalla vuelve atras
    val navigate: Boolean = false,
    @StringRes val errorMessageRes: Int? = null,
)

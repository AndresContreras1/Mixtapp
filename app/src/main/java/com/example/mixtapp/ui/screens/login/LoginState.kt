package com.example.mixtapp.ui.screens.login

import androidx.annotation.StringRes

data class LoginState(
    val email: String = "",
    val contrasena: String = "",
    val passwordVisible: Boolean = false,
    // Aviso de contrasena corta mientras se escribe. Lo calcula el ViewModel, no la pantalla
    val mostrarErrorContrasena: Boolean = false,
    // Se guarda el id del string, no el texto: el ViewModel no tiene acceso a los recursos.
    // null significa que no hay error que mostrar
    @StringRes val errorMessageRes: Int? = null,
    // La navegacion solo entra a Home cuando el ViewModel lo autoriza
    val navigate: Boolean = false,
    val cargando: Boolean = false,
)

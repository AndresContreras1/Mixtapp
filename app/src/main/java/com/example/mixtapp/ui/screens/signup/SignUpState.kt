package com.example.mixtapp.ui.screens.signup

import androidx.annotation.StringRes

data class SignUpState(
    val usuario: String = "",
    val email: String = "",
    val contrasena: String = "",
    val confirmarContrasena: String = "",
    val terminos: Boolean = false,
    val contrasenaVisible: Boolean = false,
    val confirmarVisible: Boolean = false,
    // La validacion la calcula el ViewModel, no la pantalla
    val mostrarErrorContrasenas: Boolean = false,
    // Se guarda el id del string, no el texto: el ViewModel no tiene acceso a los recursos.
    // null significa que no hay error que mostrar
    @StringRes val errorMessageRes: Int? = null,
    // La navegacion solo entra a Home cuando el ViewModel lo autoriza
    val navigate: Boolean = false,
)

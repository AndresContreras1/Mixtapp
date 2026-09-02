package com.example.mixtapp.ui.screens.signup

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
)

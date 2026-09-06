package com.example.mixtapp.ui.screens.splash

data class SplashState(
    // Mientras es true todavia no se sabe si hay sesion, asi que no se navega
    val verificando: Boolean = true,
    val hayUsuario: Boolean = false,
)

package com.example.mixtapp.data.model

data class UsuarioUi(
    val id: String,
    val seguidores: Int = 0,
    val seguidos: Int = 0,
    val siguiendo: Boolean = false,
    val esMiPerfil: Boolean = false,
    val nombre: String,
    val username: String,
    val bio: String,
    val fotoUrl: String,
)

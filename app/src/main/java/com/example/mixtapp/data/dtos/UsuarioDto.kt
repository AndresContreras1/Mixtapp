package com.example.mixtapp.data.dtos

import com.example.mixtapp.data.model.UsuarioUi

// Igual a la tabla usuarios del backend
data class UsuarioPerfilDto(
    val id: Int,
    val nombre: String,
    val email: String,
    val fotoUrl: String?,
    val createdAt: String,
    val updatedAt: String,
)

// Lo que trae el include del usuario dentro de las resenas de un album
data class UsuarioDto(
    val id: Int,
    val nombre: String,
    val fotoUrl: String?,
)

fun UsuarioPerfilDto.toUsuarioUi(): UsuarioUi {
    return UsuarioUi(
        id = id.toString(),
        nombre = nombre,
        email = email,
        fotoUrl = fotoUrl ?: "",
    )
}

package com.example.mixtapp.data.dtos

import com.example.mixtapp.data.model.UsuarioUi

data class UsuarioDto(
    val id: Int,
    val nombre: String,
    val email: String?,
    val fotoUrl: String?,
    val createdAt: String?,
    val updatedAt: String?,
)

fun UsuarioDto.toUsuarioUi(): UsuarioUi {
    return UsuarioUi(
        id = id.toString(),
        nombre = nombre,
        email = email ?: "",
        fotoUrl = fotoUrl ?: "",
    )
}

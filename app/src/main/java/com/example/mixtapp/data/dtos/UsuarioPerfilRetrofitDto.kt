package com.example.mixtapp.data.dtos

import com.example.mixtapp.data.model.UsuarioUi

// Igual a la tabla usuarios del backend
data class UsuarioPerfilRetrofitDto(
    val id: String,
    val nombre: String,
    val email: String,
    val fotoUrl: String?,
    val createdAt: String?,
    val updatedAt: String?,
) : UsuarioDtoGeneric() {

    // El backend no tiene nombre de usuario ni biografia: se toma la parte del correo antes de la arroba
    override fun toUsuarioUi(): UsuarioUi {
        return UsuarioUi(
            id = id,
            nombre = nombre,
            username = email.substringBefore("@"),
            bio = "",
            fotoUrl = fotoUrl ?: "",
        )
    }
}

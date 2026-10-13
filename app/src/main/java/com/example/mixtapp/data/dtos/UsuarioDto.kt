package com.example.mixtapp.data.dtos

// Lo que se muestra del usuario dentro de una resena (el include del backend o la copia en Firestore)
data class UsuarioDto(
    val id: String,
    val nombre: String,
    val fotoUrl: String?,
) {
    // Firestore necesita un constructor vacio para convertir el documento en el DTO
    constructor() : this("", "", null)
}

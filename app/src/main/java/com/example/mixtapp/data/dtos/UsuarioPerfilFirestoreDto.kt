package com.example.mixtapp.data.dtos

import com.example.mixtapp.data.model.UsuarioUi

// Igual al documento de la coleccion usuarios de Firestore, cuyo id es el uid de Authentication
data class UsuarioPerfilFirestoreDto(
    val id: String,
    val username: String,
    val nombre: String,
    val bio: String?,
    val fotoUrl: String?,
    val seguidoresIds: List<String> = emptyList(),
) : UsuarioDtoGeneric() {

    // Firestore necesita un constructor vacio para convertir el documento en el DTO
    constructor() : this("", "", "", null, null)

    // Los nulables se quedan en el DTO: a la pantalla llegan textos vacios
    override fun toUsuarioUi(): UsuarioUi {
        return UsuarioUi(
            id = id,
            seguidores = seguidoresIds.distinct().size,
            nombre = nombre,
            username = username,
            bio = bio ?: "",
            fotoUrl = fotoUrl ?: "",
        )
    }
}

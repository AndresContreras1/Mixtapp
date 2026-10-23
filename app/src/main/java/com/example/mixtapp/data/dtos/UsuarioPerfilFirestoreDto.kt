package com.example.mixtapp.data.dtos

import com.example.mixtapp.data.model.UsuarioUi

// Igual al documento de la coleccion usuarios de Firestore, cuyo id es el uid de Authentication
data class UsuarioPerfilFirestoreDto(
    val id: String,
    val username: String,
    val nombre: String,
    val bio: String?,
    val fotoUrl: String?,
    // Los contadores van en el documento para mostrarlos sin leer las subcolecciones
    // followers y following
    val followersCount: Int,
    val followingCount: Int,
    // El documento tambien guarda el token de notificaciones; se declara aunque el perfil no lo
    // muestre, para que toObject no avise en el Logcat "No setter/field for fcmtoken"
    val fcmtoken: String?,
    // No esta en la base: el data source lo llena buscando mi uid en la subcoleccion followers
    var followed: Boolean = false,
) : UsuarioDtoGeneric() {

    // Firestore necesita un constructor vacio para convertir el documento en el DTO
    constructor() : this("", "", "", null, null, 0, 0, null)

    // Los nulables se quedan en el DTO: a la pantalla llegan textos vacios
    override fun toUsuarioUi(): UsuarioUi {
        return UsuarioUi(
            id = id,
            seguidores = followersCount,
            seguidos = followingCount,
            siguiendo = followed,
            nombre = nombre,
            username = username,
            bio = bio ?: "",
            fotoUrl = fotoUrl ?: "",
        )
    }
}

package com.example.mixtapp.data.dtos

// Lo que se guarda del usuario en Firestore. El correo y la contrasena se quedan en
// Authentication: guardarlos tambien en la base no hace falta y es peligroso
data class RegistrarUsuarioDto(
    val username: String,
    val nombre: String,
    val bio: String?,
    val fotoUrl: String?,
)

// Lo que se puede cambiar en Editar perfil; los contadores y la foto no se tocan
data class ActualizarUsuarioDto(
    val nombre: String,
    val username: String,
    val bio: String?,
)

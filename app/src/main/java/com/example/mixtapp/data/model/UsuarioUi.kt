package com.example.mixtapp.data.model

// Un usuario para su perfil y para las listas: sus datos, sus contadores y si es mi perfil o si ya
// lo sigo, que los decide el repositorio con mi uid
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
    // Su usuario de Instagram sin la @ y el link a su perfil; vacios si no lo puso
    val instagram: String = "",
    val instagramUrl: String = "",
)

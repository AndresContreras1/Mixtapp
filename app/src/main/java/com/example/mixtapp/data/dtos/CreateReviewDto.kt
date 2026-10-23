package com.example.mixtapp.data.dtos

// Lo que se manda para crear o modificar una resena
data class CreateReviewDto(
    val usuarioId: String,
    val albumId: String,
    val calificacion: Int,
    val comentario: String,
    val fechaEscucha: String?,
    // Una resena nueva empieza con el contador en 0; al modificarla el contador no se toca
    val likesCount: Int,
    // Firestore no tiene joins: la resena guarda una copia de lo que se muestra del usuario y del
    // album. Son nulables para que no choque con el backend, que los toma de sus tablas
    val usuario: CreateReviewUsuarioDto? = null,
    val album: CreateReviewAlbumDto? = null,
)

// Solo los datos del usuario que se muestran con la resena
data class CreateReviewUsuarioDto(
    val nombre: String? = null,
    val fotoUrl: String? = null,
)

// Solo los datos del album que se muestran en Mis resenas y en el perfil
data class CreateReviewAlbumDto(
    val titulo: String? = null,
    val artista: String? = null,
    val portadaUrl: String? = null,
)

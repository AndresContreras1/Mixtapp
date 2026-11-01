package com.example.mixtapp.data.dtos

// Lo que se manda para crear o modificar una resena
data class CreateReviewDto(
    val usuarioId: String,
    val albumId: String,
    val calificacion: Int,
    val comentario: String,
    val fechaEscucha: String?,
    // Una resena nueva empieza con los contadores en 0; al modificarla los contadores no se tocan
    val likesCount: Int,
    val commentsCount: Int,
    // Fechas en formato ISO: Siguiendo y el inicio muestran primero lo ultimo que se publico o edito.
    // Al modificarla solo cambia updatedAt
    val createdAt: String?,
    val updatedAt: String,
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

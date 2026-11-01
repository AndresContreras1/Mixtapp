package com.example.mixtapp.data.dtos

// Lo que se manda para crear o modificar una resena en Firestore
data class CreateReviewDto(
    val usuarioId: String,
    val itemId: String,
    val tipo: String,
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
    // Firestore no tiene joins: la resena guarda una copia de lo que se muestra del usuario y del item
    val usuario: CreateReviewUsuarioDto,
    val item: CreateReviewItemDto,
)

// Solo los datos del usuario que se muestran con la resena
data class CreateReviewUsuarioDto(
    val nombre: String? = null,
    val fotoUrl: String? = null,
)

// Solo los datos del item que se muestran en la tarjeta de la resena
data class CreateReviewItemDto(
    val titulo: String,
    val subtitulo: String,
    val imagenUrl: String,
)

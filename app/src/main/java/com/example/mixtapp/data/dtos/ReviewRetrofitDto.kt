package com.example.mixtapp.data.dtos

import com.example.mixtapp.data.model.TIPO_ALBUM

// Cada base guarda la resena distinto, como pasa con el usuario: en el backend solo hay resenas de
// albumes, con albumId y el album en el include
data class ReviewRetrofitDto(
    val id: String,
    val calificacion: Int,
    val comentario: String?,
    val fechaEscucha: String?,
    val usuarioId: String,
    val albumId: String,
    val createdAt: String?,
    val updatedAt: String?,
    val usuario: UsuarioDto?,
    val album: AlbumDeResenaDto?,
)

// Lo que trae el include del album en la resena
data class AlbumDeResenaDto(
    val id: String,
    val titulo: String,
    val artista: String,
    val portadaUrl: String,
)

// Lo que recibe el backend para crear o modificar una resena: solo los campos de su tabla
data class CreateReviewRetrofitDto(
    val usuarioId: String,
    val albumId: String,
    val calificacion: Int,
    val comentario: String,
    val fechaEscucha: String?,
)

// La resena del backend queda igual que la de Firestore: un album, sin likes ni comentarios
fun ReviewRetrofitDto.toReviewDto(): ReviewDto {
    return ReviewDto(
        id = id,
        calificacion = calificacion,
        comentario = comentario,
        fechaEscucha = fechaEscucha,
        usuarioId = usuarioId,
        itemId = albumId,
        tipo = TIPO_ALBUM,
        createdAt = createdAt,
        updatedAt = updatedAt,
        usuario = usuario,
        item = album?.let { ItemDeResenaDto(titulo = it.titulo, subtitulo = it.artista, imagenUrl = it.portadaUrl) },
        likesCount = 0,
        commentsCount = 0,
    )
}

fun CreateReviewDto.toCreateReviewRetrofitDto(usuarioId: String): CreateReviewRetrofitDto {
    return CreateReviewRetrofitDto(
        usuarioId = usuarioId,
        albumId = itemId,
        calificacion = calificacion,
        comentario = comentario,
        fechaEscucha = fechaEscucha,
    )
}

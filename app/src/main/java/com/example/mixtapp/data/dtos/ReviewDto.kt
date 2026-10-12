package com.example.mixtapp.data.dtos

import com.example.mixtapp.data.model.MyReviewUi
import com.example.mixtapp.data.model.SongReviewItemUi

// Resena de un album, con el usuario que la escribio
data class ReviewConUsuarioDto(
    val id: Int,
    val calificacion: Int,
    val comentario: String?,
    val fechaEscucha: String?,
    val usuarioId: Int,
    val albumId: Int,
    val createdAt: String,
    val updatedAt: String,
    val usuario: UsuarioDto,
)

// Resena de un usuario, con el album que resena
data class ReviewConAlbumDto(
    val id: Int,
    val calificacion: Int,
    val comentario: String?,
    val fechaEscucha: String?,
    val usuarioId: Int,
    val albumId: Int,
    val createdAt: String,
    val updatedAt: String,
    val album: AlbumDeResenaDto,
)

fun ReviewConUsuarioDto.toSongReviewItemUi(): SongReviewItemUi {
    return SongReviewItemUi(
        id = id.toString(),
        authorId = usuarioId.toString(),
        author = usuario.nombre,
        authorImage = usuario.fotoUrl ?: "",
        fecha = fechaEscucha ?: "",
        rating = calificacion,
        content = comentario ?: "",
    )
}

fun ReviewConAlbumDto.toMyReviewUi(): MyReviewUi {
    return MyReviewUi(
        id = id.toString(),
        album = album.toAlbum(),
        rating = calificacion,
        excerpt = comentario ?: "",
        date = fechaEscucha ?: "",
    )
}

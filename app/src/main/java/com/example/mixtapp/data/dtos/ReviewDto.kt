package com.example.mixtapp.data.dtos

import com.example.mixtapp.data.model.MyReviewUi
import com.example.mixtapp.data.model.SongReviewItemUi

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

data class ReviewConAlbumDto(
    val id: Int,
    val calificacion: Int,
    val comentario: String?,
    val fechaEscucha: String?,
    val usuarioId: Int,
    val albumId: Int,
    val createdAt: String,
    val updatedAt: String,
    val album: AlbumDto,
)

fun ReviewConUsuarioDto.toSongReviewItemUi(): SongReviewItemUi {
    return SongReviewItemUi(
        id = id.toString(),
        authorId = usuarioId.toString(),
        author = usuario.nombre,
        authorImage = usuario.fotoUrl ?: "",
        daysAgo = fechaEscucha ?: "",
        rating = calificacion,
        content = comentario ?: "",
        likes = 0,
        isLiked = false,
    )
}

fun ReviewConAlbumDto.toMyReviewUi(): MyReviewUi {
    return MyReviewUi(
        id = id.toString(),
        album = album.toAlbum(),
        rating = calificacion,
        excerpt = comentario ?: "",
        tags = emptyList(),
        date = fechaEscucha ?: "",
    )
}

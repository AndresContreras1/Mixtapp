package com.example.mixtapp.data.dtos

import com.example.mixtapp.data.model.MyReviewUi
import com.example.mixtapp.data.model.SongReviewItemUi

// Resena de un album, con el usuario que la escribio
data class ReviewConUsuarioDto(
    val id: String,
    val calificacion: Int,
    val comentario: String?,
    val fechaEscucha: String?,
    val usuarioId: String,
    val albumId: String,
    val createdAt: String?,
    val updatedAt: String?,
    val usuario: UsuarioDto?,
    // El documento de Firestore tambien guarda la copia del album; se declara aunque esta lista
    // no la use, para que toObject no avise en el Logcat "No setter/field for album"
    val album: AlbumDeResenaDto?,
    val likesDeUsuarios: List<String> = emptyList(),
) {
    // Firestore necesita un constructor vacio para convertir el documento en el DTO
    constructor() : this("", 0, null, null, "", "", null, null, null, null)
}

// Resena de un usuario, con el album que resena
data class ReviewConAlbumDto(
    val id: String,
    val calificacion: Int,
    val comentario: String?,
    val fechaEscucha: String?,
    val usuarioId: String,
    val albumId: String,
    val createdAt: String?,
    val updatedAt: String?,
    // Igual que arriba: la copia del usuario tambien esta en el documento
    val usuario: UsuarioDto?,
    val album: AlbumDeResenaDto?,
    val likesDeUsuarios: List<String> = emptyList(),
) {
    constructor() : this("", 0, null, null, "", "", null, null, null, null)
}

// El repositorio pasa el id del usuario de la app para marcar su resena
fun ReviewConUsuarioDto.toSongReviewItemUi(usuarioActualId: String): SongReviewItemUi {
    return SongReviewItemUi(
        id = id,
        authorId = usuarioId,
        author = usuario?.nombre ?: "",
        authorImage = usuario?.fotoUrl ?: "",
        fecha = fechaEscucha ?: "",
        rating = calificacion,
        content = comentario ?: "",
        esMia = usuarioId == usuarioActualId,
        likes = likesDeUsuarios.distinct().size,
        isLiked = usuarioActualId in likesDeUsuarios,
    )
}

fun ReviewConAlbumDto.toMyReviewUi(): MyReviewUi {
    return MyReviewUi(
        id = id,
        album = (album ?: AlbumDeResenaDto()).toAlbum(albumId = albumId),
        rating = calificacion,
        excerpt = comentario ?: "",
        likes = likesDeUsuarios.distinct().size,
        date = fechaEscucha ?: "",
    )
}

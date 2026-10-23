package com.example.mixtapp.data.dtos

import com.example.mixtapp.data.model.AlbumReviewItemUi
import com.example.mixtapp.data.model.DiscussionReviewUi
import com.example.mixtapp.data.model.FollowingReviewUi
import com.example.mixtapp.data.model.MyReviewUi

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
    // La tarjeta solo necesita cuantos likes tiene: quien los dio esta en la subcoleccion likes
    val likesCount: Int,
    // No esta en la base: el data source lo llena buscando mi uid en la subcoleccion likes
    var liked: Boolean = false,
) {
    // Firestore necesita un constructor vacio para convertir el documento en el DTO
    constructor() : this("", 0, null, null, "", "", null, null, null, null, 0)
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
    val likesCount: Int,
    var liked: Boolean = false,
) {
    constructor() : this("", 0, null, null, "", "", null, null, null, null, 0)
}

// El repositorio pasa el id del usuario de la app para marcar su resena
fun ReviewConUsuarioDto.toAlbumReviewItemUi(usuarioActualId: String): AlbumReviewItemUi {
    return AlbumReviewItemUi(
        id = id,
        authorId = usuarioId,
        author = usuario?.nombre ?: "",
        authorImage = usuario?.fotoUrl ?: "",
        fecha = fechaEscucha ?: "",
        rating = calificacion,
        content = comentario ?: "",
        esMia = usuarioId == usuarioActualId,
        likes = likesCount,
        isLiked = liked,
    )
}

fun ReviewConAlbumDto.toMyReviewUi(): MyReviewUi {
    return MyReviewUi(
        id = id,
        album = (album ?: AlbumDeResenaDto()).toAlbum(albumId = albumId),
        rating = calificacion,
        excerpt = comentario ?: "",
        likes = likesCount,
        date = fechaEscucha ?: "",
    )
}

// Las iniciales y el nombre salen de la copia del usuario que guarda la resena; los comentarios
// llegan aparte porque la discusion no esta en este documento
fun ReviewConAlbumDto.toFollowingReviewUi(comentarios: Int): FollowingReviewUi {
    val nombre = usuario?.nombre ?: ""

    return FollowingReviewUi(
        id = id,
        authorId = usuarioId,
        reviewerName = nombre,
        reviewerInitials = nombre.take(2).uppercase(),
        reviewedAt = fechaEscucha ?: createdAt ?: "",
        album = (album ?: AlbumDeResenaDto()).toAlbum(albumId = albumId),
        rating = calificacion,
        reviewText = comentario ?: "",
        likes = likesCount,
        comments = comentarios,
        isLiked = liked,
        isShared = false,
    )
}

// La resena que encabeza su discusion
fun ReviewConAlbumDto.toDiscussionReviewUi(comentarios: Int): DiscussionReviewUi {
    val nombre = usuario?.nombre ?: ""

    return DiscussionReviewUi(
        id = id,
        reviewerName = nombre,
        reviewerAvatarText = nombre.take(2).uppercase(),
        reviewedAt = fechaEscucha ?: createdAt ?: "",
        album = (album ?: AlbumDeResenaDto()).toAlbum(albumId = albumId),
        rating = calificacion,
        reviewText = comentario ?: "",
        likes = likesCount,
        commentsCount = comentarios,
        isLiked = liked,
        isShared = false,
    )
}

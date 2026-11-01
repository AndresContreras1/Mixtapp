package com.example.mixtapp.data.dtos

import com.example.mixtapp.data.model.ItemResumenUi
import com.example.mixtapp.data.model.ReviewUi
import com.example.mixtapp.data.model.TIPO_ALBUM
import java.time.Instant

// Una resena de un album, una cancion o un artista, igual al documento de la coleccion reviews.
// Trae la copia del usuario y del item que se muestran: en Firestore no hay joins
data class ReviewDto(
    val id: String,
    val calificacion: Int,
    val comentario: String?,
    val fechaEscucha: String?,
    val usuarioId: String,
    // El id del album, la cancion o el artista; tipo dice cual de los tres es
    val itemId: String,
    val tipo: String,
    val createdAt: String?,
    val updatedAt: String?,
    val usuario: UsuarioDto?,
    val item: ItemDeResenaDto?,
    // Las tarjetas solo necesitan cuantos likes y comentarios tiene: quien los dio esta en las
    // subcolecciones likes y comments
    val likesCount: Int,
    val commentsCount: Int,
    // No esta en la base: el data source lo llena buscando mi uid en la subcoleccion likes
    var liked: Boolean = false,
) {
    // Firestore necesita un constructor vacio para convertir el documento en el DTO
    constructor() : this("", 0, null, null, "", "", TIPO_ALBUM, null, null, null, null, 0, 0)
}

// La copia del item que guarda la resena: lo que se ve en la tarjeta
data class ItemDeResenaDto(
    val titulo: String,
    val subtitulo: String,
    val imagenUrl: String,
) {
    constructor() : this("", "", "")
}

// El repositorio pasa el id del usuario de la app para marcar su resena. ahora tiene un valor por
// defecto para que las pruebas puedan pasar una fecha fija
fun ReviewDto.toReviewUi(usuarioActualId: String, ahora: Instant = Instant.now()): ReviewUi {
    return ReviewUi(
        id = id,
        authorId = usuarioId,
        author = usuario?.nombre ?: "",
        authorImage = usuario?.fotoUrl ?: "",
        // Solo AAAA-MM-DD: si no puso la fecha de escucha, el dia en que la publico
        date = fechaEscucha ?: createdAt?.take(10) ?: "",
        rating = calificacion,
        content = comentario ?: "",
        timeAgo = haceCuanto(fechaIso = updatedAt ?: createdAt, ahora = ahora),
        isMine = usuarioId == usuarioActualId,
        likes = likesCount,
        likesText = formatoCantidad(likesCount),
        isLiked = liked,
        comments = commentsCount,
        item = ItemResumenUi(
            id = itemId,
            tipo = tipo,
            titulo = item?.titulo ?: "",
            subtitulo = item?.subtitulo ?: "",
            imagenUrl = item?.imagenUrl ?: "",
        ),
        updatedAt = updatedAt ?: createdAt ?: "",
    )
}

package com.example.mixtapp.data.dtos

import com.example.mixtapp.data.model.DiscussionCommentUi
import java.time.Instant

// Comentario de la discusion de una resena: vive en la subcoleccion reviews/{id}/comments
data class ComentarioDto(
    val id: String,
    val usuarioId: String,
    // Copia de lo que se muestra de quien lo escribio, como en la resena: no hay joins
    val usuario: UsuarioDto?,
    val contenido: String,
    // null si es un comentario principal; si es una respuesta, el id del comentario que responde
    val parentCommentId: String?,
    // La tarjeta solo necesita cuantos likes tiene: quien los dio esta en su subcoleccion likes
    val likesCount: Int,
    // En formato ISO, asi que ordenar el texto ordena las fechas
    val createdAt: String?,
    // No esta en la base: el data source lo llena buscando mi uid en la subcoleccion likes
    var liked: Boolean = false,
) {
    // Firestore necesita un constructor vacio para convertir el documento en el DTO
    constructor() : this("", "", null, "", null, 0, null)
}

// Lo que se manda para crear un comentario. Empieza sin likes
data class CreateComentarioDto(
    val usuarioId: String,
    val contenido: String,
    val parentCommentId: String?,
    val likesCount: Int,
    val createdAt: String,
    val usuario: CreateReviewUsuarioDto? = null,
)

fun ComentarioDto.toDiscussionCommentUi(ahora: Instant = Instant.now()): DiscussionCommentUi {
    return DiscussionCommentUi(
        id = id,
        authorId = usuarioId,
        author = usuario?.nombre ?: "",
        authorImage = usuario?.fotoUrl ?: "",
        timeAgo = haceCuanto(fechaIso = createdAt, ahora = ahora),
        content = contenido,
        likes = likesCount,
        likesText = formatoCantidad(likesCount),
        isReply = parentCommentId != null,
        isLiked = liked,
        parentId = parentCommentId,
    )
}

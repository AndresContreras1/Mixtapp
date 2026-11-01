package com.example.mixtapp.data.model

data class DiscussionUi(
    val id: String,
    val review: ReviewUi,
    val comments: List<DiscussionCommentUi>,
)

data class DiscussionCommentUi(
    val id: String,
    val authorId: String,
    val author: String,
    // La foto de quien lo escribio; si esta vacia se muestran las iniciales
    val authorImage: String,
    // Cuanto hace que lo publico ("5 min"); vacio si fue hace menos de un minuto
    val timeAgo: String,
    val content: String,
    val likes: Int,
    val likesText: String,
    val isReply: Boolean,
    val isLiked: Boolean,
    // El comentario principal al que responde; null si no es una respuesta
    val parentId: String? = null,
)

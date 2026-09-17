package com.example.mixtapp.data.local

import com.example.mixtapp.data.model.DiscussionCommentUi
import com.example.mixtapp.data.model.DiscussionReviewUi
import com.example.mixtapp.data.model.DiscussionUi

object LocalDiscussionProvider {

    // Cada discusion se identifica con el mismo id de la resena que la abre,
    // para que la navegacion pueda buscarla por ese id
    val discussions = listOf(
        DiscussionUi(
            id = "toxicity-liz",
            review = DiscussionReviewUi(
                id = "toxicity-liz",
                reviewerName = "Liz",
                reviewerAvatarText = "Lz",
                reviewedAt = "hace 4 h",
                cover = AlbumCovers.TOXICITY,
                albumTitle = "Toxicity",
                artistName = "System of a down",
                rating = 5,
                reviewText = "\"Toxicity\" is pure chaotic genius. Blending heavy, erratic riffs with Serj Tankian's manic vocals, System of a Down turns societal overload into an insanely catchy, immortal metal anthem.",
                likes = 24,
                commentsCount = 4,
                isLiked = false,
                isShared = false,
            ),
            comments = sharedComments,
        ),
        DiscussionUi(
            id = "from-zero-jhon",
            review = DiscussionReviewUi(
                id = "from-zero-jhon",
                reviewerName = "Jhon",
                reviewerAvatarText = "Jh",
                reviewedAt = "hace 1 hora",
                cover = AlbumCovers.FROM_ZERO,
                albumTitle = "From Zero",
                artistName = "Linkin Park",
                rating = 5,
                reviewText = "From Zero is a fiery, seamless rebirth for Linkin Park. Blending raw, heavy nostalgia with fresh, high-voltage energy-fueled by Emily Armstrong's powerhouse vocals-it proves the band can honor their iconic legacy while stepping boldly into a new era.",
                likes = 24,
                commentsCount = 4,
                isLiked = false,
                isShared = false,
            ),
            comments = sharedComments,
        ),
    )
}

// Hilo de comentarios de ejemplo, compartido por las dos discusiones
private val sharedComments = listOf(
    DiscussionCommentUi(
        id = "comment-priya",
        author = "Priya",
        initials = "pk",
        timeAgo = "hace 3 h",
        content = "Lo único malo es que se acaba muy pronto. Quería 20 minutos más de este mundo. ¿Hay edición deluxe o algo?",
        likes = 12,
        isReply = false,
        isLiked = false,
    ),
    DiscussionCommentUi(
        id = "comment-leo",
        author = "Leo",
        initials = "Lb",
        timeAgo = "hace 2 h",
        content = "Nada anunciado todavía, por desgracia. Aunque circulan grabaciones en vivo de la gira.",
        likes = 4,
        isReply = true,
        isLiked = false,
    ),
    DiscussionCommentUi(
        id = "comment-alex-1",
        author = "Alex",
        initials = "Al",
        timeAgo = "hace 2 h",
        content = "No estoy de acuerdo con las 4 estrellas. Las canciones de la mitad se me hacen lentas. Igual me gusta, pero para mí es un 3.",
        likes = 6,
        isReply = false,
        isLiked = false,
    ),
    DiscussionCommentUi(
        id = "comment-soph",
        author = "Soph",
        initials = "Sp",
        timeAgo = "hace 1 h",
        content = "¿Cuáles exactamente? Me da curiosidad porque yo sentí lo contrario, el ritmo me pareció intencional.",
        likes = 4,
        isReply = false,
        isLiked = false,
    ),
    DiscussionCommentUi(
        id = "comment-alex-2",
        author = "Alex",
        initials = "Al",
        timeAgo = "hace 1 h",
        content = "Sobre todo la 6 y la 7. Por separado están bien, pero cortan el impulso.",
        likes = 1,
        isReply = true,
        isLiked = false,
    ),
)

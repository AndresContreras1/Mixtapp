package com.example.mixtapp.data.datasource

import com.example.mixtapp.data.local.LocalDiscussionProvider
import com.example.mixtapp.data.local.LocalMyReviewsProvider
import com.example.mixtapp.data.local.LocalReviewAlbumProvider
import com.example.mixtapp.data.model.Album
import com.example.mixtapp.data.model.DiscussionCommentUi
import com.example.mixtapp.data.model.DiscussionUi
import com.example.mixtapp.data.model.MyReviewUi
import javax.inject.Inject

class ReviewLocalDataSource @Inject constructor() {

    suspend fun getMyReviews(): List<MyReviewUi> = LocalMyReviewsProvider.reviews

    suspend fun getDiscussionByReviewId(reviewId: String): DiscussionUi? =
        LocalDiscussionProvider.discussions.find { it.id == reviewId }

    suspend fun getMoods(): List<String> = LocalReviewAlbumProvider.moods

    suspend fun getFechaEscuchaInicial(): String = LocalReviewAlbumProvider.fechaEscuchaInicial

    suspend fun getFechaEscuchaSugerida(): String = LocalReviewAlbumProvider.fechaEscuchaSugerida

    suspend fun publicarResena(
        album: Album,
        rating: Int,
        texto: String,
        moods: List<String>,
        fecha: String,
    ): MyReviewUi {
        val resena = MyReviewUi(
            id = "resena-propia-" + (LocalMyReviewsProvider.reviews.size + 1),
            album = album,
            rating = rating,
            excerpt = texto,
            tags = moods,
            date = fecha,
        )
        LocalMyReviewsProvider.reviews.add(0, resena)
        return resena
    }

    suspend fun publicarComentario(reviewId: String, autor: String, texto: String): DiscussionUi? =
        actualizarDiscusion(reviewId = reviewId) { discusion ->
            val comentario = DiscussionCommentUi(
                id = "comentario-propio-" + (discusion.comments.size + 1),
                author = autor,
                initials = autor.take(2).lowercase(),
                timeAgo = "ahora",
                content = texto,
                likes = 0,
                isReply = false,
                isLiked = false,
            )
            discusion.copy(comments = discusion.comments + comentario)
        }

    suspend fun darQuitarLikeResena(reviewId: String): DiscussionUi? =
        actualizarDiscusion(reviewId = reviewId) { discusion ->
            discusion.copy(review = discusion.review.copy(isLiked = !discusion.review.isLiked))
        }

    suspend fun compartirQuitarResena(reviewId: String): DiscussionUi? =
        actualizarDiscusion(reviewId = reviewId) { discusion ->
            discusion.copy(review = discusion.review.copy(isShared = !discusion.review.isShared))
        }

    suspend fun darQuitarLikeComentario(reviewId: String, commentId: String): DiscussionUi? =
        actualizarDiscusion(reviewId = reviewId) { discusion ->
            discusion.copy(
                comments = discusion.comments.map { comentario ->
                    if (comentario.id == commentId) {
                        comentario.copy(isLiked = !comentario.isLiked)
                    } else {
                        comentario
                    }
                }
            )
        }

    private fun actualizarDiscusion(
        reviewId: String,
        cambio: (DiscussionUi) -> DiscussionUi,
    ): DiscussionUi? {
        val indice = LocalDiscussionProvider.discussions.indexOfFirst { it.id == reviewId }
        if (indice == -1) return null

        val actualizada = cambio(LocalDiscussionProvider.discussions[indice])
        LocalDiscussionProvider.discussions[indice] = actualizada
        return actualizada
    }
}

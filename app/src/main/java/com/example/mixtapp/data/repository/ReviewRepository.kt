package com.example.mixtapp.data.repository

import com.example.mixtapp.data.datasource.AlbumLocalDataSource
import com.example.mixtapp.data.datasource.ReviewLocalDataSource
import com.example.mixtapp.data.model.Album
import com.example.mixtapp.data.model.DiscussionCommentUi
import com.example.mixtapp.data.model.DiscussionUi
import com.example.mixtapp.data.model.MyReviewUi
import javax.inject.Inject

class ReviewRepository @Inject constructor(
    private val reviewLocalDataSource: ReviewLocalDataSource,
    private val albumLocalDataSource: AlbumLocalDataSource
) {

    suspend fun getMyReviews(): Result<List<MyReviewUi>> {
        return try {
            Result.success(reviewLocalDataSource.getMyReviews())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDiscussionByReviewId(reviewId: String): Result<DiscussionUi?> {
        return try {
            Result.success(reviewLocalDataSource.getDiscussionByReviewId(reviewId = reviewId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMoods(): Result<List<String>> {
        return try {
            Result.success(reviewLocalDataSource.getMoods())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFechaEscuchaInicial(): Result<String> {
        return try {
            Result.success(reviewLocalDataSource.getFechaEscuchaInicial())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFechaEscuchaSugerida(): Result<String> {
        return try {
            Result.success(reviewLocalDataSource.getFechaEscuchaSugerida())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun publicarResena(
        album: Album,
        rating: Int,
        texto: String,
        moods: List<String>,
        fecha: String,
    ): Result<MyReviewUi> {
        return try {
            val resena = MyReviewUi(
                id = "resena-propia-" + (reviewLocalDataSource.getMyReviews().size + 1),
                album = album,
                rating = rating,
                excerpt = texto,
                tags = moods,
                date = fecha,
            )
            reviewLocalDataSource.agregarResena(resena = resena)
            albumLocalDataSource.calificarSongReview(songId = album.id, rating = rating)
            Result.success(resena)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun publicarComentario(reviewId: String, autor: String, texto: String): Result<DiscussionUi?> {
        return try {
            val discusion = reviewLocalDataSource.getDiscussionByReviewId(reviewId = reviewId)

            if (discusion == null) {
                Result.success(null)
            } else {
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

                Result.success(
                    reviewLocalDataSource.guardarDiscusion(
                        discusion = discusion.copy(comments = discusion.comments + comentario)
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun darQuitarLikeResena(reviewId: String): Result<DiscussionUi?> {
        return try {
            Result.success(reviewLocalDataSource.darQuitarLikeResena(reviewId = reviewId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun compartirQuitarResena(reviewId: String): Result<DiscussionUi?> {
        return try {
            Result.success(reviewLocalDataSource.compartirQuitarResena(reviewId = reviewId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun darQuitarLikeComentario(reviewId: String, commentId: String): Result<DiscussionUi?> {
        return try {
            Result.success(
                reviewLocalDataSource.darQuitarLikeComentario(reviewId = reviewId, commentId = commentId)
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

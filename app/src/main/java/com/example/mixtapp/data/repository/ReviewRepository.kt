package com.example.mixtapp.data.repository

import com.example.mixtapp.data.datasource.AlbumLocalDataSource
import com.example.mixtapp.data.datasource.ReviewLocalDataSource
import com.example.mixtapp.data.model.Album
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
            val resena = reviewLocalDataSource.publicarResena(
                album = album,
                rating = rating,
                texto = texto,
                moods = moods,
                fecha = fecha,
            )
            albumLocalDataSource.calificarSongReview(songId = album.id, rating = rating)
            Result.success(resena)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun publicarComentario(reviewId: String, autor: String, texto: String): Result<DiscussionUi?> {
        return try {
            Result.success(
                reviewLocalDataSource.publicarComentario(reviewId = reviewId, autor = autor, texto = texto)
            )
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

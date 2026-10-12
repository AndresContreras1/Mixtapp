package com.example.mixtapp.data.repository

import android.util.Log
import com.example.mixtapp.data.datasource.ReviewLocalDataSource
import com.example.mixtapp.data.datasource.impl.ReviewRetrofitDataSourceImpl
import com.example.mixtapp.data.dtos.CreateReviewDto
import com.example.mixtapp.data.model.DiscussionCommentUi
import com.example.mixtapp.data.model.DiscussionUi
import com.example.mixtapp.data.model.USUARIO_ACTUAL_ID
import retrofit2.HttpException
import javax.inject.Inject

class ReviewRepository @Inject constructor(
    private val reviewLocalDataSource: ReviewLocalDataSource,
    private val reviewRemoteDataSource: ReviewRetrofitDataSourceImpl
) {

    // Si llega el id de la resena se modifica; si no, se crea
    suspend fun guardarResena(
        albumId: String,
        rating: Int,
        texto: String,
        fecha: String,
        reviewId: String?,
    ): Result<Unit> {
        return try {
            val createReviewDto = CreateReviewDto(
                usuarioId = USUARIO_ACTUAL_ID.toInt(),
                albumId = albumId.toInt(),
                calificacion = rating,
                comentario = texto,
                fechaEscucha = if (fecha.isEmpty()) null else fecha,
            )

            if (reviewId != null) reviewRemoteDataSource.updateReview(reviewId, createReviewDto)
            else reviewRemoteDataSource.createReview(createReviewDto)

            Result.success(Unit)
        } catch (e: HttpException) {
            Log.d("ReviewRepository", "guardarResena: ${e.message}")
            Result.failure(errorDelBackend(e))
        } catch (e: Exception) {
            Log.d("ReviewRepository", "guardarResena: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun eliminarResena(reviewId: String): Result<Unit> {
        return try {
            reviewRemoteDataSource.deleteReview(reviewId)
            Result.success(Unit)
        } catch (e: HttpException) {
            Log.d("ReviewRepository", "eliminarResena: ${e.message}")
            Result.failure(errorDelBackend(e))
        } catch (e: Exception) {
            Log.d("ReviewRepository", "eliminarResena: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getDiscussionByReviewId(reviewId: String): Result<DiscussionUi> {
        return try {
            val discusion = reviewLocalDataSource.getDiscussionByReviewId(reviewId = reviewId)
                ?: return Result.failure(ContenidoNoEncontradoException())

            Result.success(discusion)
        } catch (e: Exception) {
            Log.d("ReviewRepository", "getDiscussionByReviewId: ${e.message}")
            Result.failure(ErrorDeDatosLocalesException())
        }
    }

    suspend fun publicarComentario(
        reviewId: String,
        autor: String,
        texto: String,
        esRespuesta: Boolean,
    ): Result<DiscussionUi> = actualizarDiscusion(reviewId = reviewId) { discusion ->
        val comentario = DiscussionCommentUi(
            id = "comentario-propio-" + (discusion.comments.size + 1),
            author = autor,
            initials = autor.take(2).lowercase(),
            timeAgo = "ahora",
            content = texto,
            likes = 0,
            isReply = esRespuesta,
            isLiked = false,
        )

        discusion.copy(
            review = discusion.review.copy(
                commentsCount = discusion.review.commentsCount + 1
            ),
            comments = discusion.comments + comentario,
        )
    }

    suspend fun darQuitarLikeResena(reviewId: String): Result<DiscussionUi> =
        actualizarDiscusion(reviewId = reviewId) { discusion ->
            val nuevoLike = !discusion.review.isLiked

            discusion.copy(
                review = discusion.review.copy(
                    likes = if (nuevoLike) discusion.review.likes + 1 else discusion.review.likes - 1,
                    isLiked = nuevoLike,
                )
            )
        }

    suspend fun compartirQuitarResena(reviewId: String): Result<DiscussionUi> =
        actualizarDiscusion(reviewId = reviewId) { discusion ->
            discusion.copy(review = discusion.review.copy(isShared = !discusion.review.isShared))
        }

    suspend fun darQuitarLikeComentario(
        reviewId: String,
        commentId: String,
    ): Result<DiscussionUi> = actualizarDiscusion(reviewId = reviewId) { discusion ->
        discusion.copy(
            comments = discusion.comments.map { comentario ->
                if (comentario.id == commentId) {
                    conLike(comentario = comentario)
                } else {
                    comentario
                }
            }
        )
    }

    private fun conLike(comentario: DiscussionCommentUi): DiscussionCommentUi {
        val nuevoLike = !comentario.isLiked

        return comentario.copy(
            likes = if (nuevoLike) comentario.likes + 1 else comentario.likes - 1,
            isLiked = nuevoLike,
        )
    }

    private suspend fun actualizarDiscusion(
        reviewId: String,
        cambio: (DiscussionUi) -> DiscussionUi,
    ): Result<DiscussionUi> {
        return try {
            val discusion = reviewLocalDataSource.getDiscussionByReviewId(reviewId = reviewId)
                ?: return Result.failure(ContenidoNoEncontradoException())
            val actualizada = cambio(discusion)

            reviewLocalDataSource.guardarDiscusion(discusion = actualizada)
            Result.success(actualizada)
        } catch (e: IndexOutOfBoundsException) {
            Log.d("ReviewRepository", "actualizarDiscusion: ${e.message}")
            Result.failure(ContenidoNoEncontradoException())
        } catch (e: Exception) {
            Log.d("ReviewRepository", "actualizarDiscusion: ${e.message}")
            Result.failure(ErrorDeDatosLocalesException())
        }
    }
}

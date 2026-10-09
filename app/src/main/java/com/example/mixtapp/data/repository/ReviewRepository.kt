package com.example.mixtapp.data.repository

import com.example.mixtapp.data.datasource.AlbumLocalDataSource
import com.example.mixtapp.data.datasource.ReviewLocalDataSource
import com.example.mixtapp.data.datasource.impl.ReviewRetrofitDataSourceImpl
import com.example.mixtapp.data.datasource.impl.UsuarioRetrofitDataSourceImpl
import com.example.mixtapp.data.dtos.CreateReviewDto
import com.example.mixtapp.data.dtos.toMyReviewUi
import com.example.mixtapp.data.model.Album
import com.example.mixtapp.data.model.DiscussionCommentUi
import com.example.mixtapp.data.model.DiscussionUi
import com.example.mixtapp.data.model.MyReviewUi
import retrofit2.HttpException
import javax.inject.Inject

class ReviewRepository @Inject constructor(
    private val reviewLocalDataSource: ReviewLocalDataSource,
    private val albumLocalDataSource: AlbumLocalDataSource,
    private val reviewRemoteDataSource: ReviewRetrofitDataSourceImpl,
    private val usuarioRemoteDataSource: UsuarioRetrofitDataSourceImpl
) {

    private val usuarioActualId = "1"

    suspend fun getMyReviews(): Result<List<MyReviewUi>> {
        return try {
            val reviews = usuarioRemoteDataSource.getReviewsDeUsuario(usuarioActualId)
            val misResenas = reviews.map { it.toMyReviewUi() }
            Result.success(misResenas)
        } catch (e: HttpException) {
            Result.failure(ErrorDelServidorException())
        } catch (e: Exception) {
            Result.failure(SinConexionException())
        }
    }

    suspend fun getMyReviewByAlbumId(albumId: String): Result<MyReviewUi?> {
        return try {
            val reviews = usuarioRemoteDataSource.getReviewsDeUsuario(usuarioActualId)
            val resena = reviews.find { it.albumId.toString() == albumId }
            Result.success(resena?.toMyReviewUi())
        } catch (e: HttpException) {
            Result.failure(ErrorDelServidorException())
        } catch (e: Exception) {
            Result.failure(SinConexionException())
        }
    }

    suspend fun getDiscussionByReviewId(reviewId: String): Result<DiscussionUi?> {
        return try {
            Result.success(reviewLocalDataSource.getDiscussionByReviewId(reviewId = reviewId))
        } catch (e: Exception) {
            Result.failure(ErrorDeDatosLocalesException())
        }
    }

    suspend fun getFechaEscuchaInicial(): Result<String> {
        return try {
            Result.success(reviewLocalDataSource.getFechaEscuchaInicial())
        } catch (e: Exception) {
            Result.failure(ErrorDeDatosLocalesException())
        }
    }

    suspend fun getFechaEscuchaSugerida(): Result<String> {
        return try {
            Result.success(reviewLocalDataSource.getFechaEscuchaSugerida())
        } catch (e: Exception) {
            Result.failure(ErrorDeDatosLocalesException())
        }
    }

    suspend fun publicarResena(
        album: Album,
        rating: Int,
        texto: String,
        fecha: String,
    ): Result<Unit> {
        return try {
            val reviews = usuarioRemoteDataSource.getReviewsDeUsuario(usuarioActualId)
            val existente = reviews.find { it.albumId.toString() == album.id }
            val createReviewDto = CreateReviewDto(
                usuarioId = usuarioActualId.toInt(),
                albumId = album.id.toInt(),
                calificacion = rating,
                comentario = texto,
                fechaEscucha = if (fecha.isNullOrEmpty()) null else fecha,
            )

            if (existente != null) reviewRemoteDataSource.updateReview(existente.id.toString(), createReviewDto)
            else reviewRemoteDataSource.createReview(createReviewDto)

            calificarAlbum(albumId = album.id, rating = rating)
            Result.success(Unit)
        } catch (e: HttpException) {
            Result.failure(ErrorDelServidorException())
        } catch (e: Exception) {
            Result.failure(SinConexionException())
        }
    }

    suspend fun eliminarResena(reviewId: String): Result<Unit> {
        return try {
            reviewRemoteDataSource.deleteReview(reviewId)
            Result.success(Unit)
        } catch (e: HttpException) {
            Result.failure(ErrorDelServidorException())
        } catch (e: Exception) {
            Result.failure(SinConexionException())
        }
    }

    suspend fun publicarComentario(
        reviewId: String,
        autor: String,
        texto: String,
    ): Result<DiscussionUi> = actualizarDiscusion(reviewId = reviewId) { discusion ->
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

    private suspend fun calificarAlbum(albumId: String, rating: Int) {
        val songReview = albumLocalDataSource.getSongReviewById(songId = albumId) ?: return

        albumLocalDataSource.guardarSongReview(songReview = songReview.copy(userRating = rating))
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
            Result.failure(ContenidoNoEncontradoException())
        } catch (e: Exception) {
            Result.failure(ErrorDeDatosLocalesException())
        }
    }
}

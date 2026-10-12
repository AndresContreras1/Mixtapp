package com.example.mixtapp.data.repository

import android.util.Log
import com.example.mixtapp.data.datasource.AuthRemoteDataSource
import com.example.mixtapp.data.datasource.ReviewLocalDataSource
import com.example.mixtapp.data.datasource.impl.firestore.AlbumFirestoreDataSourceImpl
import com.example.mixtapp.data.datasource.impl.firestore.ReviewFirestoreDataSourceImpl
import com.example.mixtapp.data.datasource.impl.firestore.UsuarioFirestoreDataSourceImpl
import com.example.mixtapp.data.dtos.CreateReviewAlbumDto
import com.example.mixtapp.data.dtos.CreateReviewDto
import com.example.mixtapp.data.dtos.CreateReviewUsuarioDto
import com.example.mixtapp.data.dtos.toMyReviewUi
import com.example.mixtapp.data.model.DiscussionCommentUi
import com.example.mixtapp.data.model.DiscussionUi
import com.example.mixtapp.data.model.MyReviewUi
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import retrofit2.HttpException
import javax.inject.Inject

// Organiza varios data sources: las resenas, el usuario y el album que se copian en cada una,
// y Authentication para saber quien es el usuario de la app
class ReviewRepository @Inject constructor(
    private val reviewLocalDataSource: ReviewLocalDataSource,
    private val reviewRemoteDataSource: ReviewFirestoreDataSourceImpl,
    private val usuarioRemoteDataSource: UsuarioFirestoreDataSourceImpl,
    private val albumRemoteDataSource: AlbumFirestoreDataSourceImpl,
    private val authRemoteDataSource: AuthRemoteDataSource
) {

    suspend fun getMyReviews(): Result<List<MyReviewUi>> {
        return try {
            val usuarioId = authRemoteDataSource.currentUser?.uid
                ?: return Result.failure(SinSesionException())

            val reviews = usuarioRemoteDataSource.getReviewsDeUsuario(usuarioId)
            val misResenas = reviews.map { it.toMyReviewUi() }
            Result.success(misResenas)
        } catch (e: HttpException) {
            Log.d("ReviewRepository", "getMyReviews: ${e.message}")
            Result.failure(errorDelBackend(e))
        } catch (e: FirebaseFirestoreException) {
            Log.d("ReviewRepository", "getMyReviews: ${e.message}")
            Result.failure(errorDeFirestore(e))
        } catch (e: Exception) {
            Log.d("ReviewRepository", "getMyReviews: ${e.message}")
            Result.failure(e)
        }
    }

    // Si llega el id de la resena se modifica; si no, se crea
    suspend fun guardarResena(
        albumId: String,
        rating: Int,
        texto: String,
        fecha: String,
        reviewId: String?,
    ): Result<Unit> {
        return try {
            val usuarioId = authRemoteDataSource.currentUser?.uid
                ?: return Result.failure(SinSesionException())

            val photoUrl = authRemoteDataSource.currentUser?.photoUrl?.toString()

            // La resena guarda una copia de lo que se muestra del usuario y del album, porque
            // en Firestore no hay joins. Solo se copia lo que se ve, no todo el usuario.
            // El album y mi nombre salen a la vez y se espera el mas lento
            val createReviewDto = coroutineScope {
                val album = async { albumRemoteDataSource.getAlbumById(albumId) }
                val nombre = async { nombreDelUsuario(usuarioId = usuarioId) }
                val albumDto = album.await()

                CreateReviewDto(
                    usuarioId = usuarioId,
                    albumId = albumId,
                    calificacion = rating,
                    comentario = texto,
                    fechaEscucha = if (fecha.isEmpty()) null else fecha,
                    usuario = CreateReviewUsuarioDto(nombre = nombre.await(), fotoUrl = photoUrl),
                    album = CreateReviewAlbumDto(
                        titulo = albumDto.titulo,
                        artista = albumDto.artista,
                        portadaUrl = albumDto.portadaUrl,
                    ),
                )
            }

            if (reviewId != null) reviewRemoteDataSource.updateReview(reviewId, createReviewDto)
            else reviewRemoteDataSource.createReview(createReviewDto)

            Result.success(Unit)
        } catch (e: HttpException) {
            Log.d("ReviewRepository", "guardarResena: ${e.message}")
            Result.failure(errorDelBackend(e))
        } catch (e: FirebaseFirestoreException) {
            Log.d("ReviewRepository", "guardarResena: ${e.message}")
            Result.failure(errorDeFirestore(e))
        } catch (e: NoSuchElementException) {
            Log.d("ReviewRepository", "guardarResena: ${e.message}")
            Result.failure(ContenidoNoEncontradoException())
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
        } catch (e: FirebaseFirestoreException) {
            Log.d("ReviewRepository", "eliminarResena: ${e.message}")
            Result.failure(errorDeFirestore(e))
        } catch (e: Exception) {
            Log.d("ReviewRepository", "eliminarResena: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getDiscussionByReviewId(reviewId: String): Result<DiscussionUi?> {
        return try {
            Result.success(reviewLocalDataSource.getDiscussionByReviewId(reviewId = reviewId))
        } catch (e: Exception) {
            Log.d("ReviewRepository", "getDiscussionByReviewId: ${e.message}")
            Result.failure(ErrorDeDatosLocalesException())
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

    // Una cuenta creada antes del sprint 10 no tiene documento en usuarios: se usa su correo
    private suspend fun nombreDelUsuario(usuarioId: String): String {
        return try {
            usuarioRemoteDataSource.getUsuarioById(usuarioId).toUsuarioUi().nombre
        } catch (e: NoSuchElementException) {
            // Solo una cuenta sin documento usa el correo; si Firestore falla por otra razon,
            // falla todo el guardado, como en clase
            Log.d("ReviewRepository", "nombreDelUsuario: ${e.message}")
            authRemoteDataSource.currentUser?.email?.substringBefore("@") ?: ""
        }
    }
}

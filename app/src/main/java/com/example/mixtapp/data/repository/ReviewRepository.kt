```kotlin
package com.example.mixtapp.data.repository

import android.util.Log
import com.example.mixtapp.data.datasource.AuthRemoteDataSource
import com.example.mixtapp.data.datasource.ReviewRemoteDataSource
import com.example.mixtapp.data.datasource.UsuarioRemoteDataSource
import com.example.mixtapp.data.dtos.CreateComentarioDto
import com.example.mixtapp.data.dtos.CreateReviewDto
import com.example.mixtapp.data.dtos.CreateReviewItemDto
import com.example.mixtapp.data.dtos.CreateReviewUsuarioDto
import com.example.mixtapp.data.dtos.toDiscussionCommentUi
import com.example.mixtapp.data.dtos.toReviewUi
import com.example.mixtapp.data.model.DiscussionCommentUi
import com.example.mixtapp.data.model.DiscussionUi
import com.example.mixtapp.data.model.ItemUi
import com.example.mixtapp.data.model.ReviewUi
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.time.Instant
import javax.inject.Inject

// Organiza varios data sources: las reseñas con sus comentarios,
// el usuario que se copia en cada una y Authentication.
class ReviewRepository @Inject constructor(
    private val reviewRemoteDataSource: ReviewRemoteDataSource,
    private val usuarioRemoteDataSource: UsuarioRemoteDataSource,
    private val authRemoteDataSource: AuthRemoteDataSource
) {

    // Da el like o lo quita.
    suspend fun sendOrDeleteLike(reviewId: String): Result<Unit> {
        return try {
            val usuarioId = authRemoteDataSource.currentUser?.uid
                ?: return Result.failure(SinSesionException())

            reviewRemoteDataSource.sendOrDeleteLike(
                reviewId = reviewId,
                userId = usuarioId
            )

            Result.success(Unit)

        } catch (e: HttpException) {
            Log.d("ReviewRepository", "sendOrDeleteLike: ${e.message}")
            Result.failure(errorDelBackend(e))

        } catch (e: FirebaseFirestoreException) {
            Log.d("ReviewRepository", "sendOrDeleteLike: ${e.message}")
            Result.failure(errorDeFirestore(e))

        } catch (e: Exception) {
            Log.d("ReviewRepository", "sendOrDeleteLike: ${e.message}")
            Result.failure(ErrorInesperadoException())
        }
    }

    // Mis reseñas en vivo.
    fun getMyReviewsLive(): Flow<List<ReviewUi>> {
        val usuarioId = authRemoteDataSource.currentUser?.uid ?: ""

        return usuarioRemoteDataSource.listenReviewsDeUsuario(
            usuarioId,
            currentUserId = usuarioId
        )
            .map { reviews ->
                reviews
                    .map { it.toReviewUi(usuarioActualId = usuarioId) }
                    .sortedByDescending { it.updatedAt }
            }
            .catch { e ->
                Log.d("ReviewRepository", "getMyReviewsLive: ${e.message}")
                throw errorDelFlujo(e)
            }
    }

    // Obtiene las reseñas publicadas durante las últimas 24 horas
    // que tengan coordenadas para poder mostrarlas en el mapa.
    fun getRecentReviewsLive(): Flow<List<ReviewUi>> {

        val usuarioId = authRemoteDataSource.currentUser?.uid ?: ""

        val hace24Horas = Instant.now()
            .minusSeconds(24 * 60 * 60)
            .toEpochMilli()

        return reviewRemoteDataSource
            .listenRecentReviews(hace24Horas)
            .map { reviews ->

                reviews
                    .filter { review ->
                        review.latitude != null &&
                        review.longitude != null &&
                        (review.createdAtMillis ?: 0L) >= hace24Horas
                    }
                    .map { review ->
                        review.toReviewUi(
                            usuarioActualId = usuarioId
                        )
                    }
                    .sortedByDescending { review ->
                        review.createdAtMillis ?: 0L
                    }
            }
            .catch { e ->
                Log.d(
                    "ReviewRepository",
                    "getRecentReviewsLive: ${e.message}"
                )
                throw errorDelFlujo(e)
            }
    }

    // Si llega el id de la reseña se modifica; si no, se crea.
    suspend fun guardarResena(
        item: ItemUi,
        rating: Int,
        texto: String,
        fecha: String,
        reviewId: String?,
        latitude: Double? = null,
        longitude: Double? = null,
    ): Result<Unit> {
        return try {
            val usuarioId = authRemoteDataSource.currentUser?.uid
                ?: return Result.failure(SinSesionException())

            // Se generan las fechas de creación y actualización.
            val instanteActual = Instant.now()
            val ahora = instanteActual.toString()
            val ahoraMillis = instanteActual.toEpochMilli()

            // Se guarda una copia de la información del usuario y del item.
            val createReviewDto = CreateReviewDto(
                usuarioId = usuarioId,
                itemId = item.id,
                tipo = item.tipo,
                calificacion = rating,
                comentario = texto,
                fechaEscucha = if (fecha.isEmpty()) null else fecha,
                likesCount = 0,
                commentsCount = 0,

                // La fecha de creación solo se asigna al publicar.
                createdAt = if (reviewId == null) ahora else null,

                // Fecha en milisegundos para filtrar las últimas 24 horas.
                createdAtMillis = if (reviewId == null) ahoraMillis else null,

                updatedAt = ahora,

                // Las coordenadas solo se asignan al crear la reseña.
                latitude = if (reviewId == null) latitude else null,
                longitude = if (reviewId == null) longitude else null,

                usuario = CreateReviewUsuarioDto(
                    nombre = nombreDelUsuario(usuarioId = usuarioId),
                    fotoUrl = authRemoteDataSource.currentUser?.photoUrl?.toString()
                ),

                item = CreateReviewItemDto(
                    titulo = item.titulo,
                    subtitulo = item.subtitulo,
                    imagenUrl = item.imagenUrl
                )
            )

            if (reviewId != null) {
                reviewRemoteDataSource.updateReview(
                    reviewId,
                    createReviewDto
                )
            } else {
                reviewRemoteDataSource.createReview(createReviewDto)
            }

            Result.success(Unit)

        } catch (e: HttpException) {
            Log.d("ReviewRepository", "guardarResena: ${e.message}")
            Result.failure(errorDelBackend(e))

        } catch (e: FirebaseFirestoreException) {
            Log.d("ReviewRepository", "guardarResena: ${e.message}")
            Result.failure(errorDeFirestore(e))

        } catch (e: Exception) {
            Log.d("ReviewRepository", "guardarResena: ${e.message}")
            Result.failure(ErrorInesperadoException())
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
            Result.failure(ErrorInesperadoException())
        }
    }

    // La discusión en vivo combina la reseña y sus comentarios.
    fun getDiscussionLive(reviewId: String): Flow<DiscussionUi> {
        val usuarioId = authRemoteDataSource.currentUser?.uid ?: ""

        return combine(
            reviewRemoteDataSource.listenReviewById(
                reviewId,
                currentUserId = usuarioId
            ),
            reviewRemoteDataSource.listenComentarios(
                reviewId,
                currentUserId = usuarioId
            )
        ) { review, comentarios ->

            if (review == null) {
                throw ContenidoNoEncontradoException()
            }

            DiscussionUi(
                id = reviewId,
                review = review.toReviewUi(usuarioActualId = usuarioId),
                comments = enHilo(
                    comentarios = comentarios.map {
                        it.toDiscussionCommentUi()
                    }
                )
            )
        }
            .catch { e ->
                Log.d("ReviewRepository", "getDiscussionLive: ${e.message}")
                throw errorDelFlujo(e)
            }
    }

    // Publica un comentario o una respuesta.
    suspend fun publicarComentario(
        reviewId: String,
        texto: String,
        parentCommentId: String?,
    ): Result<Unit> {
        return try {
            val usuarioId = authRemoteDataSource.currentUser?.uid
                ?: return Result.failure(SinSesionException())

            val createComentarioDto = CreateComentarioDto(
                usuarioId = usuarioId,
                contenido = texto,
                parentCommentId = parentCommentId,
                likesCount = 0,
                createdAt = Instant.now().toString(),

                usuario = CreateReviewUsuarioDto(
                    nombre = nombreDelUsuario(usuarioId = usuarioId),
                    fotoUrl = authRemoteDataSource.currentUser?.photoUrl?.toString()
                )
            )

            reviewRemoteDataSource.createComentario(
                reviewId = reviewId,
                comentario = createComentarioDto
            )

            Result.success(Unit)

        } catch (e: HttpException) {
            Log.d("ReviewRepository", "publicarComentario: ${e.message}")
            Result.failure(errorDelBackend(e))

        } catch (e: FirebaseFirestoreException) {
            Log.d("ReviewRepository", "publicarComentario: ${e.message}")
            Result.failure(errorDeFirestore(e))

        } catch (e: Exception) {
            Log.d("ReviewRepository", "publicarComentario: ${e.message}")
            Result.failure(ErrorInesperadoException())
        }
    }

    // Da like a un comentario o lo quita.
    suspend fun sendOrDeleteLikeComentario(
        reviewId: String,
        commentId: String
    ): Result<Unit> {
        return try {
            val usuarioId = authRemoteDataSource.currentUser?.uid
                ?: return Result.failure(SinSesionException())

            reviewRemoteDataSource.sendOrDeleteLikeComentario(
                reviewId = reviewId,
                comentarioId = commentId,
                userId = usuarioId
            )

            Result.success(Unit)

        } catch (e: HttpException) {
            Log.d("ReviewRepository", "sendOrDeleteLikeComentario: ${e.message}")
            Result.failure(errorDelBackend(e))

        } catch (e: FirebaseFirestoreException) {
            Log.d("ReviewRepository", "sendOrDeleteLikeComentario: ${e.message}")
            Result.failure(errorDeFirestore(e))

        } catch (e: Exception) {
            Log.d("ReviewRepository", "sendOrDeleteLikeComentario: ${e.message}")
            Result.failure(ErrorInesperadoException())
        }
    }

    // Organiza los comentarios principales junto con sus respuestas.
    private fun enHilo(
        comentarios: List<DiscussionCommentUi>
    ): List<DiscussionCommentUi> {
        val principales = comentarios.filter { it.parentId == null }

        return principales.flatMap { principal ->
            listOf(principal) + comentarios.filter {
                it.parentId == principal.id
            }
        }
    }

    // Obtiene el nombre del usuario.
    private suspend fun nombreDelUsuario(usuarioId: String): String =
        nombreDeLaSesion(
            usuarioRemoteDataSource = usuarioRemoteDataSource,
            authRemoteDataSource = authRemoteDataSource,
            usuarioId = usuarioId
        )
}

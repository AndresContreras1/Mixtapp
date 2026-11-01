package com.example.mixtapp.data.repository

import android.util.Log
import com.example.mixtapp.data.datasource.AlbumRemoteDataSource
import com.example.mixtapp.data.datasource.AuthRemoteDataSource
import com.example.mixtapp.data.datasource.ReviewRemoteDataSource
import com.example.mixtapp.data.datasource.UsuarioRemoteDataSource
import com.example.mixtapp.data.dtos.CreateComentarioDto
import com.example.mixtapp.data.dtos.CreateReviewAlbumDto
import com.example.mixtapp.data.dtos.CreateReviewDto
import com.example.mixtapp.data.dtos.CreateReviewUsuarioDto
import com.example.mixtapp.data.dtos.toDiscussionCommentUi
import com.example.mixtapp.data.dtos.toDiscussionReviewUi
import com.example.mixtapp.data.dtos.toMyReviewUi
import com.example.mixtapp.data.model.DiscussionCommentUi
import com.example.mixtapp.data.model.DiscussionUi
import com.example.mixtapp.data.model.MyReviewUi
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.time.Instant
import javax.inject.Inject

// Organiza varios data sources: las resenas con sus comentarios, el usuario y el album que se
// copian en cada una, y Authentication para saber quien es el usuario de la app.
// Recibe los contratos: para volver al backend de Express basta con cambiar los @Binds de
// DataSourceModule a las implementaciones de Retrofit
class ReviewRepository @Inject constructor(
    private val reviewRemoteDataSource: ReviewRemoteDataSource,
    private val usuarioRemoteDataSource: UsuarioRemoteDataSource,
    private val albumRemoteDataSource: AlbumRemoteDataSource,
    private val authRemoteDataSource: AuthRemoteDataSource
) {

    // Da el like o lo quita: el data source decide segun si ya existe. La UI cambia el corazon
    // y el contador solo si esto sale bien
    suspend fun sendOrDeleteLike(reviewId: String): Result<Unit> {
        return try {
            val usuarioId = authRemoteDataSource.currentUser?.uid
                ?: return Result.failure(SinSesionException())

            reviewRemoteDataSource.sendOrDeleteLike(reviewId = reviewId, userId = usuarioId)
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

    // Mis resenas en vivo: un like en otro celular cambia el contador sin salir de la pantalla.
    // Como en clase, retorna el flujo y no un Result: los errores los recibe el ViewModel en el catch
    fun getMyReviewsLive(): Flow<List<MyReviewUi>> {
        val usuarioId = authRemoteDataSource.currentUser?.uid ?: ""

        return usuarioRemoteDataSource.listenReviewsDeUsuario(usuarioId)
            .map { reviews -> reviews.map { it.toMyReviewUi() } }
            .catch { e ->
                Log.d("ReviewRepository", "getMyReviewsLive: ${e.message}")
                throw errorDelFlujo(e)
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
            // El album y mi nombre salen a la vez y se espera el mas lento. Si el album no
            // existe queda null
            val createReviewDto = coroutineScope {
                val album = async { albumRemoteDataSource.getAlbumById(albumId) }
                val nombre = async { nombreDelUsuario(usuarioId = usuarioId) }
                val albumDto = album.await() ?: return@coroutineScope null

                CreateReviewDto(
                    usuarioId = usuarioId,
                    albumId = albumId,
                    calificacion = rating,
                    comentario = texto,
                    fechaEscucha = if (fecha.isEmpty()) null else fecha,
                    likesCount = 0,
                    commentsCount = 0,
                    usuario = CreateReviewUsuarioDto(nombre = nombre.await(), fotoUrl = photoUrl),
                    album = CreateReviewAlbumDto(
                        titulo = albumDto.titulo,
                        artista = albumDto.artista,
                        portadaUrl = albumDto.portadaUrl,
                    ),
                )
            }

            // El data source retorna null si no existe: el repositorio es el que decide el error
            if (createReviewDto == null) return Result.failure(ContenidoNoEncontradoException())

            if (reviewId != null) reviewRemoteDataSource.updateReview(reviewId, createReviewDto)
            else reviewRemoteDataSource.createReview(createReviewDto)

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

    // La discusion en vivo con dos listeners: la resena (mi like y sus contadores) y sus comentarios.
    // Cuando llega cualquiera de los dos, combine arma la discusion de nuevo, como en Seguidos
    fun getDiscussionLive(reviewId: String): Flow<DiscussionUi> {
        val usuarioId = authRemoteDataSource.currentUser?.uid ?: ""

        return combine(
            reviewRemoteDataSource.listenReviewById(reviewId, currentUserId = usuarioId),
            reviewRemoteDataSource.listenComentarios(reviewId, currentUserId = usuarioId),
        ) { review, comentarios ->
            DiscussionUi(
                id = reviewId,
                review = review.toDiscussionReviewUi(),
                comments = enHilo(comentarios = comentarios.map { it.toDiscussionCommentUi() }),
            )
        }
            .catch { e ->
                Log.d("ReviewRepository", "getDiscussionLive: ${e.message}")
                throw errorDelFlujo(e)
            }
    }

    // El comentario guarda una copia de mi nombre y mi foto, igual que la resena. Si es una
    // respuesta, llega el id del comentario principal
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
                // En formato ISO, para que la lista se ordene por esta fecha
                createdAt = Instant.now().toString(),
                usuario = CreateReviewUsuarioDto(
                    nombre = nombreDelUsuario(usuarioId = usuarioId),
                    fotoUrl = authRemoteDataSource.currentUser?.photoUrl?.toString(),
                ),
            )

            reviewRemoteDataSource.createComentario(reviewId = reviewId, comentario = createComentarioDto)
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

    // Da el like al comentario o lo quita: el data source decide segun si ya existe
    suspend fun darQuitarLikeComentario(reviewId: String, commentId: String): Result<Unit> {
        return try {
            val usuarioId = authRemoteDataSource.currentUser?.uid
                ?: return Result.failure(SinSesionException())

            reviewRemoteDataSource.sendOrDeleteLikeComentario(
                reviewId = reviewId,
                comentarioId = commentId,
                userId = usuarioId,
            )
            Result.success(Unit)
        } catch (e: HttpException) {
            Log.d("ReviewRepository", "darQuitarLikeComentario: ${e.message}")
            Result.failure(errorDelBackend(e))
        } catch (e: FirebaseFirestoreException) {
            Log.d("ReviewRepository", "darQuitarLikeComentario: ${e.message}")
            Result.failure(errorDeFirestore(e))
        } catch (e: Exception) {
            Log.d("ReviewRepository", "darQuitarLikeComentario: ${e.message}")
            Result.failure(ErrorInesperadoException())
        }
    }

    // Los comentarios llegan del mas viejo al mas nuevo: cada comentario principal va seguido de
    // sus respuestas
    private fun enHilo(comentarios: List<DiscussionCommentUi>): List<DiscussionCommentUi> {
        val principales = comentarios.filter { it.parentId == null }

        return principales.flatMap { principal ->
            listOf(principal) + comentarios.filter { it.parentId == principal.id }
        }
    }

    // Una cuenta creada antes del sprint 10 no tiene documento en usuarios: se usa su correo.
    // Si Firestore falla por otra razon, la excepcion sigue y falla todo el guardado, como en clase
    private suspend fun nombreDelUsuario(usuarioId: String): String {
        val usuario = usuarioRemoteDataSource.getUsuarioById(usuarioId)

        return usuario?.toUsuarioUi()?.nombre
            ?: authRemoteDataSource.currentUser?.email?.substringBefore("@")
            ?: ""
    }
}

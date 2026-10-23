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
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import javax.inject.Inject

// Organiza varios data sources: las resenas, el usuario y el album que se copian en cada una,
// los comentarios locales y Authentication para saber quien es el usuario de la app.
// Para volver al backend de Express basta con cambiar las implementaciones a las de Retrofit
class ReviewRepository @Inject constructor(
    private val reviewLocalDataSource: ReviewLocalDataSource,
    private val reviewRemoteDataSource: ReviewFirestoreDataSourceImpl,
    private val usuarioRemoteDataSource: UsuarioFirestoreDataSourceImpl,
    private val albumRemoteDataSource: AlbumFirestoreDataSourceImpl,
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
                    likesCount = 0,
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

    // La resena llega en vivo de la base remota, con mi like, y sus comentarios de los datos locales
    fun getDiscussionLive(reviewId: String): Flow<DiscussionUi> {
        val usuarioId = authRemoteDataSource.currentUser?.uid ?: ""

        return reviewRemoteDataSource.listenReviewById(reviewId, currentUserId = usuarioId)
            .map { review ->
                val comentarios = reviewLocalDataSource.getComentarios(reviewId = reviewId)

                DiscussionUi(
                    id = reviewId,
                    review = review.toDiscussionReviewUi(comentarios = comentarios.size),
                    comments = comentarios,
                )
            }
            .catch { e ->
                Log.d("ReviewRepository", "getDiscussionLive: ${e.message}")
                throw errorDelFlujo(e)
            }
    }

    suspend fun publicarComentario(
        reviewId: String,
        autor: String,
        texto: String,
        esRespuesta: Boolean,
    ): Result<List<DiscussionCommentUi>> = actualizarComentarios(reviewId = reviewId) { comentarios ->
        val comentario = DiscussionCommentUi(
            id = "comentario-propio-" + (comentarios.size + 1),
            author = autor,
            initials = autor.take(2).lowercase(),
            // Vacio: la pantalla muestra "ahora" desde strings.xml
            timeAgo = "",
            content = texto,
            likes = 0,
            isReply = esRespuesta,
            isLiked = false,
        )

        comentarios + comentario
    }

    suspend fun darQuitarLikeComentario(
        reviewId: String,
        commentId: String,
    ): Result<List<DiscussionCommentUi>> = actualizarComentarios(reviewId = reviewId) { comentarios ->
        comentarios.map { comentario ->
            if (comentario.id == commentId) {
                conLike(comentario = comentario)
            } else {
                comentario
            }
        }
    }

    private fun conLike(comentario: DiscussionCommentUi): DiscussionCommentUi {
        val nuevoLike = !comentario.isLiked

        return comentario.copy(
            likes = if (nuevoLike) comentario.likes + 1 else comentario.likes - 1,
            isLiked = nuevoLike,
        )
    }

    private suspend fun actualizarComentarios(
        reviewId: String,
        cambio: (List<DiscussionCommentUi>) -> List<DiscussionCommentUi>,
    ): Result<List<DiscussionCommentUi>> {
        return try {
            val comentarios = reviewLocalDataSource.getComentarios(reviewId = reviewId)
            val actualizados = cambio(comentarios)

            reviewLocalDataSource.guardarComentarios(reviewId = reviewId, comentarios = actualizados)
            Result.success(actualizados)
        } catch (e: Exception) {
            Log.d("ReviewRepository", "actualizarComentarios: ${e.message}")
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

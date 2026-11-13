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
import com.example.mixtapp.data.model.Coordenadas
import com.example.mixtapp.data.model.DiscussionCommentUi
import com.example.mixtapp.data.model.DiscussionUi
import com.example.mixtapp.data.model.ItemUi
import com.example.mixtapp.data.model.ResenaEnMapaUi
import com.example.mixtapp.data.model.ReviewUi
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.time.Instant
import javax.inject.Inject

// El mapa muestra las resenas publicadas en las ultimas 24 horas, para no saturarlo
private const val HORAS_DEL_MAPA = 24L

// Organiza varios data sources: las resenas con sus comentarios, el usuario que se copia en cada
// una y Authentication para saber quien es el usuario de la app.
// Recibe los contratos: para volver al backend de Express basta con cambiar los @Binds de
// DataSourceModule a las implementaciones de Retrofit
class ReviewRepository @Inject constructor(
    private val reviewRemoteDataSource: ReviewRemoteDataSource,
    private val usuarioRemoteDataSource: UsuarioRemoteDataSource,
    private val authRemoteDataSource: AuthRemoteDataSource
) {

    // Da el like o lo quita: el data source decide segun si ya existe. El corazon y el contador
    // cambian cuando llega la resena nueva por el listener
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

    // Mis resenas en vivo, la ultima publicada o editada primero: un like en otro celular cambia el
    // contador sin salir de la pantalla. Como en clase, retorna el flujo y no un Result: los errores
    // los recibe el ViewModel en el catch
    fun getMyReviewsLive(): Flow<List<ReviewUi>> {
        val usuarioId = authRemoteDataSource.currentUser?.uid ?: ""

        return usuarioRemoteDataSource.listenReviewsDeUsuario(usuarioId, currentUserId = usuarioId)
            .map { reviews ->
                // Las fechas van en formato ISO, asi que ordenar el texto ordena las fechas
                reviews.map { it.toReviewUi(usuarioActualId = usuarioId) }.sortedByDescending { it.updatedAt }
            }
            .catch { e ->
                Log.d("ReviewRepository", "getMyReviewsLive: ${e.message}")
                throw errorDelFlujo(e)
            }
    }

    // Si llega el id de la resena se modifica; si no, se crea. El item llega de Escribir resena,
    // que ya lo tiene cargado: de ahi sale la copia que guarda la resena
    suspend fun guardarResena(
        item: ItemUi,
        rating: Int,
        texto: String,
        fecha: String,
        reviewId: String?,
        // Donde estoy al publicar; null si no hay permiso o no se pudo leer
        ubicacion: Coordenadas? = null,
    ): Result<Unit> {
        return try {
            val usuarioId = authRemoteDataSource.currentUser?.uid
                ?: return Result.failure(SinSesionException())

            // En formato ISO, como los comentarios: la resena nueva guarda las dos fechas y al
            // modificarla solo cambia updatedAt
            val ahora = Instant.now().toString()

            // La resena guarda una copia de lo que se muestra del usuario y del item, porque en
            // Firestore no hay joins. Solo se copia lo que se ve, no todo el usuario
            val createReviewDto = CreateReviewDto(
                usuarioId = usuarioId,
                itemId = item.id,
                tipo = item.tipo,
                calificacion = rating,
                comentario = texto,
                fechaEscucha = if (fecha.isEmpty()) null else fecha,
                likesCount = 0,
                commentsCount = 0,
                createdAt = if (reviewId == null) ahora else null,
                updatedAt = ahora,
                usuario = CreateReviewUsuarioDto(
                    nombre = nombreDelUsuario(usuarioId = usuarioId),
                    fotoUrl = authRemoteDataSource.currentUser?.photoUrl?.toString(),
                ),
                item = CreateReviewItemDto(
                    titulo = item.titulo,
                    subtitulo = item.subtitulo,
                    imagenUrl = item.imagenUrl,
                ),
                // Al modificarla el data source no cambia la ubicacion: queda la de cuando se publico
                latitud = ubicacion?.latitud,
                longitud = ubicacion?.longitud,
            )

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

    // Las resenas de las ultimas 24 horas que tienen ubicacion, la mas nueva primero: cada una es
    // un marcador del mapa. Una nueva llega sola por el listener
    fun getResenasEnElMapaLive(ahora: Instant = Instant.now()): Flow<List<ResenaEnMapaUi>> {
        val usuarioId = authRemoteDataSource.currentUser?.uid ?: ""
        // En formato ISO, como createdAt
        val desde = ahora.minusSeconds(HORAS_DEL_MAPA * 60 * 60).toString()

        return reviewRemoteDataSource.listenReviewsDesde(desde = desde)
            .map { reviews ->
                reviews
                    .sortedByDescending { it.createdAt }
                    .mapNotNull { review ->
                        val latitud = review.latitud
                        val longitud = review.longitud
                        // Sin ubicacion no hay donde poner el marcador
                        if (latitud == null || longitud == null) {
                            null
                        } else {
                            ResenaEnMapaUi(
                                review = review.toReviewUi(usuarioActualId = usuarioId, ahora = ahora),
                                latitud = latitud,
                                longitud = longitud,
                            )
                        }
                    }
            }
            .catch { e ->
                Log.d("ReviewRepository", "getResenasEnElMapaLive: ${e.message}")
                throw errorDelFlujo(e)
            }
    }

    // La discusion en vivo con dos listeners: la resena (mi like y sus contadores) y sus comentarios.
    // Cuando llega cualquiera de los dos, combine arma la discusion de nuevo, como en Siguiendo
    fun getDiscussionLive(reviewId: String): Flow<DiscussionUi> {
        val usuarioId = authRemoteDataSource.currentUser?.uid ?: ""

        return combine(
            reviewRemoteDataSource.listenReviewById(reviewId, currentUserId = usuarioId),
            reviewRemoteDataSource.listenComentarios(reviewId, currentUserId = usuarioId),
        ) { review, comentarios ->
            // El data source envia null si la resena no existe: el repositorio decide el error
            if (review == null) throw ContenidoNoEncontradoException()

            DiscussionUi(
                id = reviewId,
                review = review.toReviewUi(usuarioActualId = usuarioId),
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
    suspend fun sendOrDeleteLikeComentario(reviewId: String, commentId: String): Result<Unit> {
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

    // Los comentarios llegan del mas viejo al mas nuevo: cada comentario principal va seguido de
    // sus respuestas
    private fun enHilo(comentarios: List<DiscussionCommentUi>): List<DiscussionCommentUi> {
        val principales = comentarios.filter { it.parentId == null }

        return principales.flatMap { principal ->
            listOf(principal) + comentarios.filter { it.parentId == principal.id }
        }
    }

    // Una cuenta creada antes del sprint 10 no tiene documento en usuarios: se usa su correo
    private suspend fun nombreDelUsuario(usuarioId: String): String =
        nombreDeLaSesion(
            usuarioRemoteDataSource = usuarioRemoteDataSource,
            authRemoteDataSource = authRemoteDataSource,
            usuarioId = usuarioId,
        )
}

package com.example.mixtapp.data.repository

import android.util.Log
import com.example.mixtapp.data.datasource.AlbumLocalDataSource
import com.example.mixtapp.data.datasource.AlbumRemoteDataSource
import com.example.mixtapp.data.datasource.AuthRemoteDataSource
import com.example.mixtapp.data.dtos.AlbumDto
import com.example.mixtapp.data.dtos.toAlbum
import com.example.mixtapp.data.dtos.toAlbumReviewItemUi
import com.example.mixtapp.data.model.Album
import com.example.mixtapp.data.model.SearchCategoryUi
import com.example.mixtapp.data.model.AlbumReviewItemUi
import com.example.mixtapp.data.model.AlbumReviewsUi
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import javax.inject.Inject
import kotlin.math.roundToInt

// Desde esta calificacion se cuenta que la resena recomienda el album
private const val CALIFICACION_PARA_RECOMENDAR = 4

// Recibe el contrato: para volver al backend de Express basta con cambiar el @Binds de
// DataSourceModule a AlbumRetrofitDataSourceImpl.
// Authentication dice quien es el usuario de la app: con eso se marca mi resena y mis likes
class AlbumRepository @Inject constructor(
    private val albumLocalDataSource: AlbumLocalDataSource,
    private val albumRemoteDataSource: AlbumRemoteDataSource,
    private val authRemoteDataSource: AuthRemoteDataSource
) {

    // Cada album llega con sus resenas para calcular el promedio y cuantos lo recomiendan
    suspend fun getAlbumReviews(): Result<List<AlbumReviewsUi>> {
        return try {
            val albumes = albumRemoteDataSource.getAlbumes()
            val usuarioActualId = authRemoteDataSource.currentUser?.uid ?: ""

            // Las resenas de todos los albumes se piden a la vez y se espera la mas lenta. Si una
            // falla, falla todo: un album sin sus resenas mostraria un promedio que no es
            val albumesConResenas = coroutineScope {
                val pendientes = albumes.map { album ->
                    async {
                        // Esta lista no pinta corazones: no se busca mi like de cada resena
                        val reviews = albumRemoteDataSource.getReviewsDeAlbum(album.id)
                        conEstadisticas(
                            album = album.toAlbum(),
                            reviews = reviews.map { review ->
                                review.toAlbumReviewItemUi(usuarioActualId = usuarioActualId)
                            }
                        )
                    }
                }

                pendientes.map { it.await() }
            }

            Result.success(albumesConResenas)
        } catch (e: HttpException) {
            Log.d("AlbumRepository", "getAlbumReviews: ${e.message}")
            Result.failure(errorDelBackend(e))
        } catch (e: FirebaseFirestoreException) {
            Log.d("AlbumRepository", "getAlbumReviews: ${e.message}")
            Result.failure(errorDeFirestore(e))
        } catch (e: Exception) {
            Log.d("AlbumRepository", "getAlbumReviews: ${e.message}")
            Result.failure(ErrorInesperadoException())
        }
    }

    // Datos en vivo para el inicio: cada vez que cambia una resena se arman de nuevo los albumes con
    // sus estadisticas. Como en clase, retorna el flujo y no un Result: los errores los recibe el
    // ViewModel en el catch
    fun getAlbumReviewsLive(): Flow<List<AlbumReviewsUi>> {
        val usuarioActualId = authRemoteDataSource.currentUser?.uid ?: ""
        // Los albumes no cambian mientras se usa la app: se piden con la primera lista y no en
        // cada cambio, para no gastar lecturas
        var albumes: List<AlbumDto> = emptyList()

        // El inicio no pinta corazones: no se busca mi like de cada resena
        return albumRemoteDataSource.listenAllReviews()
            .map { reviews ->
                if (albumes.isEmpty()) albumes = albumRemoteDataSource.getAlbumes()

                albumes.map { album ->
                    conEstadisticas(
                        album = album.toAlbum(),
                        reviews = reviews
                            .filter { review -> review.albumId == album.id }
                            .map { review -> review.toAlbumReviewItemUi(usuarioActualId = usuarioActualId) }
                    )
                }
            }
            .catch { e ->
                Log.d("AlbumRepository", "getAlbumReviewsLive: ${e.message}")
                throw errorDelFlujo(e)
            }
    }

    // El detalle en vivo: cada vez que cambia una resena del album (otra resena, un like en otro
    // celular) llegan de nuevo sus estadisticas. Como en clase, retorna el flujo y no un Result
    fun getAlbumReviewsByIdLive(albumId: String): Flow<AlbumReviewsUi> {
        val usuarioActualId = authRemoteDataSource.currentUser?.uid ?: ""
        // El album no cambia mientras se ve: se pide con la primera lista y no en cada cambio
        var albumGuardado: Album? = null

        return albumRemoteDataSource.listenReviewsDeAlbum(albumId, currentUserId = usuarioActualId)
            .map { reviews ->
                // Si no existe, el error va al catch del flujo y se muestra como no encontrado
                val album = albumGuardado
                    ?: albumRemoteDataSource.getAlbumById(albumId)?.toAlbum()
                    ?: throw ContenidoNoEncontradoException()
                albumGuardado = album

                conEstadisticas(
                    album = album,
                    reviews = reviews.map { review -> review.toAlbumReviewItemUi(usuarioActualId = usuarioActualId) }
                )
            }
            .catch { e ->
                Log.d("AlbumRepository", "getAlbumReviewsByIdLive: ${e.message}")
                throw errorDelFlujo(e)
            }
    }

    // El album con todas sus resenas; entre ellas va la mia si ya lo califique
    suspend fun getAlbumReviewsById(albumId: String): Result<AlbumReviewsUi> {
        return try {
            val usuarioActualId = authRemoteDataSource.currentUser?.uid ?: ""

            // El album y sus resenas salen a la vez. Si el album no existe queda null
            val albumReviews = coroutineScope {
                val album = async { albumRemoteDataSource.getAlbumById(albumId) }
                // Escribir resena solo busca la mia, sin likes
                val reviews = async { albumRemoteDataSource.getReviewsDeAlbum(albumId) }
                val albumDto = album.await() ?: return@coroutineScope null

                conEstadisticas(
                    album = albumDto.toAlbum(),
                    reviews = reviews.await().map { review ->
                        review.toAlbumReviewItemUi(usuarioActualId = usuarioActualId)
                    }
                )
            }

            // El data source retorna null si no existe: el repositorio es el que decide el error
            if (albumReviews == null) return Result.failure(ContenidoNoEncontradoException())

            Result.success(albumReviews)
        } catch (e: HttpException) {
            Log.d("AlbumRepository", "getAlbumReviewsById: ${e.message}")
            Result.failure(errorDelBackend(e))
        } catch (e: FirebaseFirestoreException) {
            Log.d("AlbumRepository", "getAlbumReviewsById: ${e.message}")
            Result.failure(errorDeFirestore(e))
        } catch (e: Exception) {
            Log.d("AlbumRepository", "getAlbumReviewsById: ${e.message}")
            Result.failure(ErrorInesperadoException())
        }
    }

    suspend fun getSearchCategories(): Result<List<SearchCategoryUi>> {
        return try {
            Result.success(albumLocalDataSource.getSearchCategories())
        } catch (e: Exception) {
            Log.d("AlbumRepository", "getSearchCategories: ${e.message}")
            Result.failure(ErrorDeDatosLocalesException())
        }
    }

    // El promedio, el total, el porcentaje que recomienda y mi calificacion salen de las resenas
    private fun conEstadisticas(album: Album, reviews: List<AlbumReviewItemUi>): AlbumReviewsUi {
        var suma = 0
        var recomiendan = 0

        reviews.forEach { review ->
            suma += review.rating
            if (review.rating >= CALIFICACION_PARA_RECOMENDAR) recomiendan++
        }

        val promedio = if (reviews.isEmpty()) 0.0 else suma.toDouble() / reviews.size
        val porcentaje = if (reviews.isEmpty()) 0 else recomiendan * 100 / reviews.size
        val miResena = reviews.find { it.esMia }

        return AlbumReviewsUi(
            id = album.id,
            album = album,
            rating = promedio,
            estrellas = promedio.roundToInt(),
            ratingCount = reviews.size.toString(),
            recommendRate = porcentaje,
            userRating = miResena?.rating ?: 0,
            reviews = reviews,
        )
    }
}

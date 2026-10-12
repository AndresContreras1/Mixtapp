package com.example.mixtapp.data.repository

import android.util.Log
import com.example.mixtapp.data.datasource.AlbumLocalDataSource
import com.example.mixtapp.data.datasource.AuthRemoteDataSource
import com.example.mixtapp.data.datasource.impl.firestore.AlbumFirestoreDataSourceImpl
import com.example.mixtapp.data.dtos.toAlbum
import com.example.mixtapp.data.dtos.toSongReviewItemUi
import com.example.mixtapp.data.model.Album
import com.example.mixtapp.data.model.SearchCategoryUi
import com.example.mixtapp.data.model.SongReviewItemUi
import com.example.mixtapp.data.model.SongReviewUi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import retrofit2.HttpException
import javax.inject.Inject
import kotlin.math.roundToInt

// Desde esta calificacion se cuenta que la resena recomienda el album
private const val CALIFICACION_PARA_RECOMENDAR = 4

// Para volver al backend de Express basta con cambiar la implementacion a AlbumRetrofitDataSourceImpl.
// Authentication dice quien es el usuario de la app, para marcar su resena
class AlbumRepository @Inject constructor(
    private val albumLocalDataSource: AlbumLocalDataSource,
    private val albumRemoteDataSource: AlbumFirestoreDataSourceImpl,
    private val authRemoteDataSource: AuthRemoteDataSource
) {

    // Cada album llega con sus resenas para calcular el promedio y cuantos lo recomiendan
    suspend fun getSongReviews(): Result<List<SongReviewUi>> {
        return try {
            val albumes = albumRemoteDataSource.getAlbumes()
            // Sin sesion ninguna resena es mia
            val usuarioActualId = authRemoteDataSource.currentUser?.uid ?: ""

            // Las resenas de todos los albumes se piden a la vez y se espera la mas lenta
            val songReviews = coroutineScope {
                val pendientes = albumes.map { album ->
                    async {
                        val reviews = albumRemoteDataSource.getReviewsDeAlbum(album.id)
                        conEstadisticas(
                            album = album.toAlbum(),
                            reviews = reviews.map { review ->
                                review.toSongReviewItemUi(usuarioActualId = usuarioActualId)
                            }
                        )
                    }
                }

                pendientes.map { it.await() }
            }

            Result.success(songReviews)
        } catch (e: HttpException) {
            Log.d("AlbumRepository", "getSongReviews: ${e.message}")
            Result.failure(errorDelBackend(e))
        } catch (e: Exception) {
            Log.d("AlbumRepository", "getSongReviews: ${e.message}")
            Result.failure(e)
        }
    }

    // El album con todas sus resenas; entre ellas va la mia si ya lo califique
    suspend fun getSongReviewById(songId: String): Result<SongReviewUi> {
        return try {
            val usuarioActualId = authRemoteDataSource.currentUser?.uid ?: ""

            // El album y sus resenas salen a la vez
            val songReview = coroutineScope {
                val album = async { albumRemoteDataSource.getAlbumById(songId) }
                val reviews = async { albumRemoteDataSource.getReviewsDeAlbum(songId) }

                conEstadisticas(
                    album = album.await().toAlbum(),
                    reviews = reviews.await().map { review ->
                        review.toSongReviewItemUi(usuarioActualId = usuarioActualId)
                    }
                )
            }

            Result.success(songReview)
        } catch (e: HttpException) {
            Log.d("AlbumRepository", "getSongReviewById: ${e.message}")
            Result.failure(errorDelBackend(e))
        } catch (e: NoSuchElementException) {
            Log.d("AlbumRepository", "getSongReviewById: ${e.message}")
            Result.failure(ContenidoNoEncontradoException())
        } catch (e: Exception) {
            Log.d("AlbumRepository", "getSongReviewById: ${e.message}")
            Result.failure(e)
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

    // El promedio, el total, el porcentaje que recomienda y mi calificacion salen de las resenas.
    // Es publica para que el ViewModel recalcule al quitar una resena, sin volver a pedir el album
    fun conEstadisticas(album: Album, reviews: List<SongReviewItemUi>): SongReviewUi {
        var suma = 0
        var recomiendan = 0

        reviews.forEach { review ->
            suma += review.rating
            if (review.rating >= CALIFICACION_PARA_RECOMENDAR) recomiendan++
        }

        val promedio = if (reviews.isEmpty()) 0.0 else suma.toDouble() / reviews.size
        val porcentaje = if (reviews.isEmpty()) 0 else recomiendan * 100 / reviews.size
        val miResena = reviews.find { it.esMia }

        return SongReviewUi(
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

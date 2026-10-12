package com.example.mixtapp.data.repository

import android.util.Log
import com.example.mixtapp.data.datasource.AlbumLocalDataSource
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

// Desde esta calificacion se cuenta que la resena recomienda el album
private const val CALIFICACION_PARA_RECOMENDAR = 4

// Para volver al backend de Express basta con cambiar la implementacion a AlbumRetrofitDataSourceImpl
class AlbumRepository @Inject constructor(
    private val albumLocalDataSource: AlbumLocalDataSource,
    private val albumRemoteDataSource: AlbumFirestoreDataSourceImpl
) {

    suspend fun getAlbumes(): Result<List<Album>> {
        return try {
            val albumes = albumRemoteDataSource.getAlbumes()
            Result.success(albumes.map { it.toAlbum() })
        } catch (e: HttpException) {
            Log.d("AlbumRepository", "getAlbumes: ${e.message}")
            Result.failure(errorDelBackend(e))
        } catch (e: Exception) {
            Log.d("AlbumRepository", "getAlbumes: ${e.message}")
            Result.failure(e)
        }
    }

    // Cada album llega con sus resenas para calcular el promedio y cuantos lo recomiendan
    suspend fun getSongReviews(): Result<List<SongReviewUi>> {
        return try {
            val albumes = albumRemoteDataSource.getAlbumes()

            // Las resenas de todos los albumes se piden a la vez y se espera la mas lenta
            val songReviews = coroutineScope {
                val pendientes = albumes.map { album ->
                    async {
                        val reviews = albumRemoteDataSource.getReviewsDeAlbum(album.id)
                        conEstadisticas(
                            album = album.toAlbum(),
                            reviews = reviews.map { it.toSongReviewItemUi() }
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

    suspend fun getSongReviewById(songId: String): Result<SongReviewUi> {
        return try {
            // El album y sus resenas salen a la vez
            val songReview = coroutineScope {
                val album = async { albumRemoteDataSource.getAlbumById(songId) }
                val reviews = async { albumRemoteDataSource.getReviewsDeAlbum(songId) }

                conEstadisticas(
                    album = album.await().toAlbum(),
                    reviews = reviews.await().map { it.toSongReviewItemUi() }
                )
            }

            Result.success(songReview)
        } catch (e: HttpException) {
            Log.d("AlbumRepository", "getSongReviewById: ${e.message}")
            Result.failure(errorDelBackend(e))
        } catch (e: Exception) {
            Log.d("AlbumRepository", "getSongReviewById: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getAlbumById(albumId: String): Result<Album> {
        return try {
            val album = albumRemoteDataSource.getAlbumById(albumId)
            Result.success(album.toAlbum())
        } catch (e: HttpException) {
            Log.d("AlbumRepository", "getAlbumById: ${e.message}")
            Result.failure(errorDelBackend(e))
        } catch (e: Exception) {
            Log.d("AlbumRepository", "getAlbumById: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getSearchCategories(): Result<List<SearchCategoryUi>> {
        return try {
            Result.success(albumLocalDataSource.getSearchCategories())
        } catch (e: Exception) {
            Result.failure(ErrorDeDatosLocalesException())
        }
    }

    // El promedio, el total y el porcentaje que recomienda salen de las resenas guardadas
    private fun conEstadisticas(album: Album, reviews: List<SongReviewItemUi>): SongReviewUi {
        var suma = 0
        var recomiendan = 0

        reviews.forEach { review ->
            suma += review.rating
            if (review.rating >= CALIFICACION_PARA_RECOMENDAR) recomiendan++
        }

        val promedio = if (reviews.isEmpty()) 0.0 else suma.toDouble() / reviews.size
        val porcentaje = if (reviews.isEmpty()) 0 else recomiendan * 100 / reviews.size

        return SongReviewUi(
            id = album.id,
            album = album,
            rating = promedio,
            ratingCount = reviews.size.toString(),
            recommendRate = porcentaje,
            userRating = 0,
            reviews = reviews,
        )
    }
}

package com.example.mixtapp.data.repository

import android.util.Log
import com.example.mixtapp.data.datasource.AlbumLocalDataSource
import com.example.mixtapp.data.datasource.AuthRemoteDataSource
import com.example.mixtapp.data.datasource.impl.firestore.AlbumFirestoreDataSourceImpl
import com.example.mixtapp.data.datasource.impl.firestore.UsuarioFirestoreDataSourceImpl
import com.example.mixtapp.data.dtos.toAlbum
import com.example.mixtapp.data.dtos.toAlbumReviewItemUi
import com.example.mixtapp.data.model.Album
import com.example.mixtapp.data.model.SearchCategoryUi
import com.example.mixtapp.data.model.AlbumReviewItemUi
import com.example.mixtapp.data.model.AlbumReviewsUi
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import retrofit2.HttpException
import javax.inject.Inject
import kotlin.math.roundToInt

// Desde esta calificacion se cuenta que la resena recomienda el album
private const val CALIFICACION_PARA_RECOMENDAR = 4

// Para volver al backend de Express basta con cambiar las implementaciones a las de Retrofit.
// Authentication dice quien es el usuario de la app y el data source de usuarios con que id lo
// guarda la base; con eso se marca mi resena
class AlbumRepository @Inject constructor(
    private val albumLocalDataSource: AlbumLocalDataSource,
    private val albumRemoteDataSource: AlbumFirestoreDataSourceImpl,
    private val usuarioRemoteDataSource: UsuarioFirestoreDataSourceImpl,
    private val authRemoteDataSource: AuthRemoteDataSource
) {

    // Cada album llega con sus resenas para calcular el promedio y cuantos lo recomiendan
    suspend fun getAlbumReviews(): Result<List<AlbumReviewsUi>> {
        return try {
            val albumes = albumRemoteDataSource.getAlbumes()
            val usuarioActualId = idDeMiUsuario()

            // Las resenas de todos los albumes se piden a la vez y se espera la mas lenta. Si una
            // falla, falla todo: un album sin sus resenas mostraria un promedio que no es
            val albumesConResenas = coroutineScope {
                val pendientes = albumes.map { album ->
                    async {
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

    // El album con todas sus resenas; entre ellas va la mia si ya lo califique
    suspend fun getAlbumReviewsById(albumId: String): Result<AlbumReviewsUi> {
        return try {
            val usuarioActualId = idDeMiUsuario()

            // El album y sus resenas salen a la vez
            val albumReviews = coroutineScope {
                val album = async { albumRemoteDataSource.getAlbumById(albumId) }
                val reviews = async { albumRemoteDataSource.getReviewsDeAlbum(albumId) }

                conEstadisticas(
                    album = album.await().toAlbum(),
                    reviews = reviews.await().map { review ->
                        review.toAlbumReviewItemUi(usuarioActualId = usuarioActualId)
                    }
                )
            }

            Result.success(albumReviews)
        } catch (e: HttpException) {
            Log.d("AlbumRepository", "getAlbumReviewsById: ${e.message}")
            Result.failure(errorDelBackend(e))
        } catch (e: FirebaseFirestoreException) {
            Log.d("AlbumRepository", "getAlbumReviewsById: ${e.message}")
            Result.failure(errorDeFirestore(e))
        } catch (e: NoSuchElementException) {
            Log.d("AlbumRepository", "getAlbumReviewsById: ${e.message}")
            Result.failure(ContenidoNoEncontradoException())
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

    // El id con el que la base guarda mis resenas. Con Firestore, sin sesion ninguna resena es mia;
    // con Retrofit el usuario de la app siempre es el 1 del backend
    private fun idDeMiUsuario(): String =
        usuarioRemoteDataSource.idDelUsuarioEnLaBase(uid = authRemoteDataSource.currentUser?.uid ?: "")

    // El promedio, el total, el porcentaje que recomienda y mi calificacion salen de las resenas.
    // Es publica para que el ViewModel recalcule al quitar una resena, sin volver a pedir el album
    fun conEstadisticas(album: Album, reviews: List<AlbumReviewItemUi>): AlbumReviewsUi {
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

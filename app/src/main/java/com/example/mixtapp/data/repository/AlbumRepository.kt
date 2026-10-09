package com.example.mixtapp.data.repository

import com.example.mixtapp.data.datasource.AlbumLocalDataSource
import com.example.mixtapp.data.datasource.impl.AlbumRetrofitDataSourceImpl
import com.example.mixtapp.data.dtos.toAlbum
import com.example.mixtapp.data.dtos.toSongReviewItemUi
import com.example.mixtapp.data.model.Album
import com.example.mixtapp.data.model.SearchCategoryUi
import com.example.mixtapp.data.model.SongReviewItemUi
import com.example.mixtapp.data.model.SongReviewUi
import retrofit2.HttpException
import javax.inject.Inject

class AlbumRepository @Inject constructor(
    private val albumLocalDataSource: AlbumLocalDataSource,
    private val albumRemoteDataSource: AlbumRetrofitDataSourceImpl
) {

    suspend fun getSongReviews(): Result<List<SongReviewUi>> {
        return try {
            val albumes = albumRemoteDataSource.getAlbumes()
            val songReviews = albumes.map { conDatosLocales(album = it.toAlbum()) }
            Result.success(songReviews)
        } catch (e: HttpException) {
            Result.failure(ErrorDelServidorException())
        } catch (e: Exception) {
            Result.failure(SinConexionException())
        }
    }

    suspend fun getTrendingSongReview(): Result<SongReviewUi> {
        return try {
            val tendencia = albumLocalDataSource.getTrendingSongReview()
            val album = albumRemoteDataSource.getAlbumById(tendencia.id)
            Result.success(tendencia.copy(album = album.toAlbum()))
        } catch (e: HttpException) {
            Result.failure(ErrorDelServidorException())
        } catch (e: IndexOutOfBoundsException) {
            Result.failure(ContenidoNoEncontradoException())
        } catch (e: Exception) {
            Result.failure(SinConexionException())
        }
    }

    suspend fun getSongReviewById(songId: String): Result<SongReviewUi> {
        return try {
            val album = albumRemoteDataSource.getAlbumById(songId)
            val reviews = albumRemoteDataSource.getReviewsDeAlbum(songId)
            val reviewsInfo = reviews.map { it.toSongReviewItemUi() }
            val songReview = conDatosLocales(album = album.toAlbum()).copy(reviews = reviewsInfo)
            Result.success(songReview)
        } catch (e: HttpException) {
            Result.failure(ErrorDelServidorException())
        } catch (e: Exception) {
            Result.failure(SinConexionException())
        }
    }

    suspend fun getAlbumById(albumId: String): Result<Album> {
        return try {
            val album = albumRemoteDataSource.getAlbumById(albumId)
            Result.success(album.toAlbum())
        } catch (e: HttpException) {
            Result.failure(ErrorDelServidorException())
        } catch (e: Exception) {
            Result.failure(SinConexionException())
        }
    }

    suspend fun getAlbumPorDefecto(): Result<Album> {
        return try {
            Result.success(albumLocalDataSource.getAlbumPorDefecto())
        } catch (e: Exception) {
            Result.failure(ErrorDeDatosLocalesException())
        }
    }

    suspend fun getSearchCategories(): Result<List<SearchCategoryUi>> {
        return try {
            Result.success(albumLocalDataSource.getSearchCategories())
        } catch (e: Exception) {
            Result.failure(ErrorDeDatosLocalesException())
        }
    }

    suspend fun calificarSongReview(songReview: SongReviewUi, rating: Int): Result<SongReviewUi> =
        actualizarSongReview(songReview = songReview) { it.copy(userRating = rating) }

    suspend fun guardarQuitarSongReview(songReview: SongReviewUi): Result<SongReviewUi> =
        actualizarSongReview(songReview = songReview) { it.copy(isSaved = !it.isSaved) }

    suspend fun darQuitarLikeSongReview(songReview: SongReviewUi): Result<SongReviewUi> =
        actualizarSongReview(songReview = songReview) { it.copy(isLiked = !it.isLiked) }

    fun darQuitarLikeResenaDeAlbum(
        songReview: SongReviewUi,
        reviewId: String,
    ): Result<SongReviewUi> {
        val actualizado = songReview.copy(
            reviews = songReview.reviews.map { resena ->
                if (resena.id == reviewId) conLike(resena) else resena
            }
        )

        return Result.success(actualizado)
    }

    private suspend fun conDatosLocales(album: Album): SongReviewUi {
        val local = albumLocalDataSource.getSongReviewById(songId = album.id)

        return local?.copy(album = album) ?: SongReviewUi(
            id = album.id,
            album = album,
            rating = 0.0,
            ratingCount = "0",
            recommendRate = 0,
            userRating = 0,
            isSaved = false,
            isLiked = false,
            reviews = emptyList(),
        )
    }

    private fun conLike(resena: SongReviewItemUi): SongReviewItemUi {
        val nuevoLike = !resena.isLiked

        return resena.copy(
            likes = if (nuevoLike) resena.likes + 1 else resena.likes - 1,
            isLiked = nuevoLike,
        )
    }

    private suspend fun actualizarSongReview(
        songReview: SongReviewUi,
        cambio: (SongReviewUi) -> SongReviewUi,
    ): Result<SongReviewUi> {
        return try {
            val local = albumLocalDataSource.getSongReviewById(songId = songReview.id)
                ?: return Result.failure(ContenidoNoEncontradoException())

            albumLocalDataSource.guardarSongReview(songReview = cambio(local))
            Result.success(cambio(songReview))
        } catch (e: IndexOutOfBoundsException) {
            Result.failure(ContenidoNoEncontradoException())
        } catch (e: Exception) {
            Result.failure(ErrorDeDatosLocalesException())
        }
    }
}

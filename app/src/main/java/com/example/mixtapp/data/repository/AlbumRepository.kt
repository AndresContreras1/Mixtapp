package com.example.mixtapp.data.repository

import com.example.mixtapp.data.datasource.AlbumLocalDataSource
import com.example.mixtapp.data.model.Album
import com.example.mixtapp.data.model.SearchCategoryUi
import com.example.mixtapp.data.model.SongReviewItemUi
import com.example.mixtapp.data.model.SongReviewUi
import javax.inject.Inject

class AlbumRepository @Inject constructor(
    private val albumLocalDataSource: AlbumLocalDataSource
) {

    suspend fun getSongReviews(): Result<List<SongReviewUi>> {
        return try {
            Result.success(albumLocalDataSource.getSongReviews())
        } catch (e: NoSuchElementException) {
            Result.failure(ContenidoNoEncontradoException())
        } catch (e: Exception) {
            Result.failure(ErrorDeDatosLocalesException())
        }
    }

    suspend fun getPopularSongReviews(): Result<List<SongReviewUi>> {
        return try {
            Result.success(albumLocalDataSource.getPopularSongReviews())
        } catch (e: NoSuchElementException) {
            Result.failure(ContenidoNoEncontradoException())
        } catch (e: Exception) {
            Result.failure(ErrorDeDatosLocalesException())
        }
    }

    suspend fun getTrendingSongReview(): Result<SongReviewUi> {
        return try {
            Result.success(albumLocalDataSource.getTrendingSongReview())
        } catch (e: IndexOutOfBoundsException) {
            Result.failure(ContenidoNoEncontradoException())
        } catch (e: Exception) {
            Result.failure(ErrorDeDatosLocalesException())
        }
    }

    suspend fun getSongReviewById(songId: String): Result<SongReviewUi?> {
        return try {
            Result.success(albumLocalDataSource.getSongReviewById(songId = songId))
        } catch (e: Exception) {
            Result.failure(ErrorDeDatosLocalesException())
        }
    }

    suspend fun getAlbumById(albumId: String): Result<Album?> {
        return try {
            Result.success(albumLocalDataSource.getAlbumById(albumId = albumId))
        } catch (e: Exception) {
            Result.failure(ErrorDeDatosLocalesException())
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

    suspend fun calificarSongReview(songId: String, rating: Int): Result<SongReviewUi> =
        actualizarSongReview(songId = songId) { it.copy(userRating = rating) }

    suspend fun guardarQuitarSongReview(songId: String): Result<SongReviewUi> =
        actualizarSongReview(songId = songId) { it.copy(isSaved = !it.isSaved) }

    suspend fun darQuitarLikeSongReview(songId: String): Result<SongReviewUi> =
        actualizarSongReview(songId = songId) { it.copy(isLiked = !it.isLiked) }

    suspend fun darQuitarLikeResenaDeAlbum(
        songId: String,
        reviewId: String,
    ): Result<SongReviewUi> = actualizarSongReview(songId = songId) { songReview ->
        songReview.copy(
            reviews = songReview.reviews.map { resena ->
                if (resena.id == reviewId) conLike(resena) else resena
            }
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
        songId: String,
        cambio: (SongReviewUi) -> SongReviewUi,
    ): Result<SongReviewUi> {
        return try {
            val songReview = albumLocalDataSource.getSongReviewById(songId = songId)
                ?: return Result.failure(ContenidoNoEncontradoException())
            val actualizado = cambio(songReview)

            albumLocalDataSource.guardarSongReview(songReview = actualizado)
            Result.success(actualizado)
        } catch (e: IndexOutOfBoundsException) {
            Result.failure(ContenidoNoEncontradoException())
        } catch (e: Exception) {
            Result.failure(ErrorDeDatosLocalesException())
        }
    }
}

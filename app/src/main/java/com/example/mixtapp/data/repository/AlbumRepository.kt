package com.example.mixtapp.data.repository

import com.example.mixtapp.data.datasource.AlbumLocalDataSource
import com.example.mixtapp.data.model.Album
import com.example.mixtapp.data.model.SearchCategoryUi
import com.example.mixtapp.data.model.SongReviewUi
import javax.inject.Inject

class AlbumRepository @Inject constructor(
    private val albumLocalDataSource: AlbumLocalDataSource
) {

    suspend fun getSongReviews(): Result<List<SongReviewUi>> {
        return try {
            Result.success(albumLocalDataSource.getSongReviews())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPopularSongReviews(): Result<List<SongReviewUi>> {
        return try {
            Result.success(albumLocalDataSource.getPopularSongReviews())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTrendingSongReview(): Result<SongReviewUi> {
        return try {
            Result.success(albumLocalDataSource.getTrendingSongReview())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSongReviewById(songId: String): Result<SongReviewUi?> {
        return try {
            Result.success(albumLocalDataSource.getSongReviewById(songId = songId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAlbumById(albumId: String): Result<Album?> {
        return try {
            Result.success(albumLocalDataSource.getAlbumById(albumId = albumId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAlbumPorDefecto(): Result<Album> {
        return try {
            Result.success(albumLocalDataSource.getAlbumPorDefecto())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSearchCategories(): Result<List<SearchCategoryUi>> {
        return try {
            Result.success(albumLocalDataSource.getSearchCategories())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun calificarSongReview(songId: String, rating: Int): Result<SongReviewUi?> {
        return try {
            Result.success(albumLocalDataSource.calificarSongReview(songId = songId, rating = rating))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun guardarQuitarSongReview(songId: String): Result<SongReviewUi?> {
        return try {
            Result.success(albumLocalDataSource.guardarQuitarSongReview(songId = songId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun darQuitarLikeSongReview(songId: String): Result<SongReviewUi?> {
        return try {
            Result.success(albumLocalDataSource.darQuitarLikeSongReview(songId = songId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun darQuitarLikeResenaDeAlbum(songId: String, reviewId: String): Result<SongReviewUi?> {
        return try {
            Result.success(
                albumLocalDataSource.darQuitarLikeResenaDeAlbum(songId = songId, reviewId = reviewId)
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

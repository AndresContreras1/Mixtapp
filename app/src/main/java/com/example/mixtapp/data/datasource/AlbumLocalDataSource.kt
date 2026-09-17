package com.example.mixtapp.data.datasource

import com.example.mixtapp.data.local.LocalAlbumProvider
import com.example.mixtapp.data.local.LocalSearchCategoriesProvider
import com.example.mixtapp.data.local.LocalSongReviewProvider
import com.example.mixtapp.data.model.Album
import com.example.mixtapp.data.model.SearchCategoryUi
import com.example.mixtapp.data.model.SongReviewUi
import javax.inject.Inject

class AlbumLocalDataSource @Inject constructor() {

    suspend fun getSongReviews(): List<SongReviewUi> = LocalSongReviewProvider.songs

    suspend fun getPopularSongReviews(): List<SongReviewUi> = LocalSongReviewProvider.popularSongs

    suspend fun getTrendingSongReview(): SongReviewUi = LocalSongReviewProvider.trendingSong

    suspend fun getSongReviewById(songId: String): SongReviewUi? =
        LocalSongReviewProvider.songs.find { it.album.id == songId }

    suspend fun getAlbumById(albumId: String): Album? =
        LocalAlbumProvider.albums.find { it.id == albumId }

    suspend fun getAlbumPorDefecto(): Album = LocalAlbumProvider.albumPorDefecto

    suspend fun getSearchCategories(): List<SearchCategoryUi> = LocalSearchCategoriesProvider.categories

    suspend fun calificarSongReview(songId: String, rating: Int): SongReviewUi? =
        actualizarSongReview(songId = songId) { it.copy(userRating = rating) }

    suspend fun guardarQuitarSongReview(songId: String): SongReviewUi? =
        actualizarSongReview(songId = songId) { it.copy(isSaved = !it.isSaved) }

    suspend fun darQuitarLikeSongReview(songId: String): SongReviewUi? =
        actualizarSongReview(songId = songId) { it.copy(isLiked = !it.isLiked) }

    suspend fun darQuitarLikeResenaDeAlbum(songId: String, reviewId: String): SongReviewUi? =
        actualizarSongReview(songId = songId) { songReview ->
            songReview.copy(
                reviews = songReview.reviews.map { resena ->
                    if (resena.id == reviewId) resena.copy(isLiked = !resena.isLiked) else resena
                }
            )
        }

    private fun actualizarSongReview(
        songId: String,
        cambio: (SongReviewUi) -> SongReviewUi,
    ): SongReviewUi? {
        val indice = LocalSongReviewProvider.songs.indexOfFirst { it.album.id == songId }
        if (indice == -1) return null

        val actualizado = cambio(LocalSongReviewProvider.songs[indice])
        LocalSongReviewProvider.songs[indice] = actualizado
        return actualizado
    }
}

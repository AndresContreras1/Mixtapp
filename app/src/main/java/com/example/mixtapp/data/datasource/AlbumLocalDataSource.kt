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

    suspend fun getSearchCategories(): List<SearchCategoryUi> = LocalSearchCategoriesProvider.categories
}

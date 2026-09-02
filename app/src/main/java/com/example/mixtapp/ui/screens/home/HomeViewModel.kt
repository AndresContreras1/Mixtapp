package com.example.mixtapp.ui.screens.home

import androidx.lifecycle.ViewModel
import com.example.mixtapp.data.local.LocalFriendActivityProvider
import com.example.mixtapp.data.local.LocalSongReviewProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeState())
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    // No recibe parametros, asi que los datos se cargan al crear el ViewModel
    init {
        getAlbums()
    }

    private fun getAlbums() {
        _uiState.update {
            it.copy(
                albums = LocalSongReviewProvider.popularSongs,
                trending = LocalSongReviewProvider.trendingSong,
                friendActivity = LocalFriendActivityProvider.friendActivity,
            )
        }
    }

    fun updateSelectedFilter(index: Int) {
        _uiState.update { it.copy(selectedFilterIndex = index) }
    }
}

package com.example.mixtapp.ui.screens.songreview

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.mixtapp.data.local.LocalSongReviewProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SongReviewsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(SongReviewsState())
    val uiState: StateFlow<SongReviewsState> = _uiState.asStateFlow()

    // Buscar la cancion es responsabilidad del ViewModel, no de la navegacion
    fun getSongById(songId: String) {
        val song = LocalSongReviewProvider.songs.find { cancion -> cancion.id == songId }

        _uiState.update {
            it.copy(
                song = song,
                userRating = song?.userRating ?: 0,
                isSaved = song?.isSaved ?: false,
                isLiked = song?.isLiked ?: false,
            )
        }
    }

    fun updateUserRating(rating: Int) {
        _uiState.update { it.copy(userRating = rating) }
    }

    fun guardarQuitarGuardado() {
        val valorActual = _uiState.value.isSaved
        _uiState.update { it.copy(isSaved = !valorActual) }
    }

    fun darQuitarLike() {
        val valorActual = _uiState.value.isLiked
        _uiState.update { it.copy(isLiked = !valorActual) }
    }
}

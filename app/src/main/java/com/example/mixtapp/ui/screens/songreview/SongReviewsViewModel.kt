package com.example.mixtapp.ui.screens.songreview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.data.model.SongReviewUi
import com.example.mixtapp.data.repository.AlbumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongReviewsViewModel @Inject constructor(
    private val albumRepository: AlbumRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SongReviewsState())
    val uiState: StateFlow<SongReviewsState> = _uiState.asStateFlow()

    // Buscar la cancion es responsabilidad del ViewModel, no de la navegacion
    fun getSongById(songId: String) {
        if (_uiState.value.song != null) return

        viewModelScope.launch {
            val result = albumRepository.getSongReviewById(songId = songId)

            if (result.isSuccess) {
                val song = result.getOrNull()

                _uiState.update {
                    it.copy(
                        song = song,
                        userRating = song?.userRating ?: 0,
                        isSaved = song?.isSaved ?: false,
                        isLiked = song?.isLiked ?: false,
                        likedReviewIds = song?.reviews
                            ?.filter { resena -> resena.isLiked }
                            ?.map { resena -> resena.id }
                            ?.toSet()
                            ?: emptySet(),
                    )
                }
            }
        }
    }

    fun updateUserRating(rating: Int) {
        val songId = _uiState.value.song?.album?.id ?: return

        viewModelScope.launch {
            val result = albumRepository.calificarSongReview(songId = songId, rating = rating)

            if (result.isSuccess) {
                actualizarSong(song = result.getOrNull())
            }
        }
    }

    fun guardarQuitarGuardado() {
        val songId = _uiState.value.song?.album?.id ?: return

        viewModelScope.launch {
            val result = albumRepository.guardarQuitarSongReview(songId = songId)

            if (result.isSuccess) {
                actualizarSong(song = result.getOrNull())
            }
        }
    }

    fun darQuitarLike() {
        val songId = _uiState.value.song?.album?.id ?: return

        viewModelScope.launch {
            val result = albumRepository.darQuitarLikeSongReview(songId = songId)

            if (result.isSuccess) {
                actualizarSong(song = result.getOrNull())
            }
        }
    }

    fun darQuitarLikeResena(reviewId: String) {
        val songId = _uiState.value.song?.album?.id ?: return

        viewModelScope.launch {
            val result = albumRepository.darQuitarLikeResenaDeAlbum(
                songId = songId,
                reviewId = reviewId,
            )

            if (result.isSuccess) {
                actualizarSong(song = result.getOrNull())
            }
        }
    }

    private fun actualizarSong(song: SongReviewUi?) {
        if (song == null) return

        _uiState.update {
            it.copy(
                song = song,
                userRating = song.userRating,
                isSaved = song.isSaved,
                isLiked = song.isLiked,
                likedReviewIds = song.reviews
                    .filter { resena -> resena.isLiked }
                    .map { resena -> resena.id }
                    .toSet(),
            )
        }
    }
}

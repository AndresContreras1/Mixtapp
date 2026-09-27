package com.example.mixtapp.ui.screens.songreview

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.model.SongReviewUi
import com.example.mixtapp.data.repository.AlbumRepository
import com.example.mixtapp.data.repository.ContenidoNoEncontradoException
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
                _uiState.update { it.copy(song = result.getOrNull(), errorMessageRes = null) }
            } else {
                _uiState.update {
                    it.copy(
                        errorMessageRes = mensajeDeError(
                            error = result.exceptionOrNull(),
                            generico = R.string.error_cargar_contenido,
                        )
                    )
                }
            }
        }
    }

    fun updateUserRating(rating: Int) {
        val songId = _uiState.value.song?.id ?: return
        val calificacionActual = _uiState.value.song?.userRating ?: 0
        val nueva = if (rating == calificacionActual) 0 else rating

        viewModelScope.launch {
            val result = albumRepository.calificarSongReview(songId = songId, rating = nueva)

            actualizarDesde(result = result, generico = R.string.error_calificar)
        }
    }

    fun guardarQuitarGuardado() {
        val songId = _uiState.value.song?.id ?: return

        viewModelScope.launch {
            val result = albumRepository.guardarQuitarSongReview(songId = songId)

            actualizarDesde(result = result, generico = R.string.error_guardar_album)
        }
    }

    fun darQuitarLike() {
        val songId = _uiState.value.song?.id ?: return

        viewModelScope.launch {
            val result = albumRepository.darQuitarLikeSongReview(songId = songId)

            actualizarDesde(result = result, generico = R.string.error_me_gusta)
        }
    }

    fun darQuitarLikeResena(reviewId: String) {
        val songId = _uiState.value.song?.id ?: return

        viewModelScope.launch {
            val result = albumRepository.darQuitarLikeResenaDeAlbum(
                songId = songId,
                reviewId = reviewId,
            )

            actualizarDesde(result = result, generico = R.string.error_me_gusta)
        }
    }

    private fun actualizarDesde(
        result: Result<SongReviewUi>,
        @StringRes generico: Int,
    ) {
        if (result.isSuccess) {
            _uiState.update { it.copy(song = result.getOrNull(), errorMessageRes = null) }
        } else {
            _uiState.update {
                it.copy(
                    errorMessageRes = mensajeDeError(
                        error = result.exceptionOrNull(),
                        generico = generico,
                    )
                )
            }
        }
    }

    @StringRes
    private fun mensajeDeError(error: Throwable?, @StringRes generico: Int): Int = when (error) {
        is ContenidoNoEncontradoException -> R.string.contenido_no_encontrado
        else -> generico
    }
}

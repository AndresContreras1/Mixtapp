package com.example.mixtapp.ui.screens.songreview

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.model.SongReviewUi
import com.example.mixtapp.data.repository.AlbumRepository
import com.example.mixtapp.data.repository.ContenidoNoEncontradoException
import com.example.mixtapp.data.repository.ErrorDelServidorException
import com.example.mixtapp.data.repository.SinConexionException
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
        val song = _uiState.value.song ?: return
        val nueva = if (rating == song.userRating) 0 else rating

        viewModelScope.launch {
            val result = albumRepository.calificarSongReview(songReview = song, rating = nueva)

            actualizarDesde(result = result, generico = R.string.error_calificar)
        }
    }

    fun guardarQuitarGuardado() {
        val song = _uiState.value.song ?: return

        viewModelScope.launch {
            val result = albumRepository.guardarQuitarSongReview(songReview = song)

            actualizarDesde(result = result, generico = R.string.error_guardar_album)
        }
    }

    fun darQuitarLike() {
        val song = _uiState.value.song ?: return

        viewModelScope.launch {
            val result = albumRepository.darQuitarLikeSongReview(songReview = song)

            actualizarDesde(result = result, generico = R.string.error_me_gusta)
        }
    }

    fun darQuitarLikeResena(reviewId: String) {
        val song = _uiState.value.song ?: return

        val result = albumRepository.darQuitarLikeResenaDeAlbum(
            songReview = song,
            reviewId = reviewId,
        )

        actualizarDesde(result = result, generico = R.string.error_me_gusta)
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
        is ErrorDelServidorException -> R.string.error_servidor
        is SinConexionException -> R.string.error_sin_conexion
        else -> generico
    }
}

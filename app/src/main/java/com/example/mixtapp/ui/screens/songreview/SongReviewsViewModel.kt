package com.example.mixtapp.ui.screens.songreview

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.repository.AlbumRepository
import com.example.mixtapp.data.repository.ContenidoNoEncontradoException
import com.example.mixtapp.data.repository.ErrorDelServidorException
import com.example.mixtapp.data.repository.ReviewRepository
import com.example.mixtapp.data.repository.SinConexionException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongReviewsViewModel @Inject constructor(
    private val albumRepository: AlbumRepository,
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SongReviewsState())
    val uiState: StateFlow<SongReviewsState> = _uiState.asStateFlow()

    // Buscar el album es responsabilidad del ViewModel, no de la navegacion
    fun getSongById(songId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }

            // El album con sus resenas y mi resena salen a la vez
            val albumPendiente = async { albumRepository.getSongReviewById(songId = songId) }
            val miResenaPendiente = async { reviewRepository.getMyReviewByAlbumId(albumId = songId) }

            val result = albumPendiente.await()
            val miResena = miResenaPendiente.await()
            val song = result.getOrNull()

            if (result.isSuccess && miResena.isSuccess && song != null) {
                val resena = miResena.getOrNull()

                _uiState.update {
                    it.copy(
                        song = song.copy(
                            userRating = resena?.rating ?: 0,
                            // Mi resena va primero para que se vea apenas entro al album
                            reviews = song.reviews.sortedByDescending { review -> review.id == resena?.id },
                        ),
                        miResenaId = resena?.id,
                        isLoading = false,
                        errorMessageRes = null,
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessageRes = mensajeDeError(
                            error = result.exceptionOrNull() ?: miResena.exceptionOrNull(),
                            generico = R.string.error_cargar_contenido,
                        )
                    )
                }
            }
        }
    }

    // Quitar la resena cambia el promedio y el total, asi que el detalle se vuelve a pedir
    fun eliminarMiResena() {
        val song = _uiState.value.song ?: return
        val miResenaId = _uiState.value.miResenaId ?: return

        viewModelScope.launch {
            val result = reviewRepository.eliminarResena(reviewId = miResenaId)

            if (result.isSuccess) {
                getSongById(songId = song.id)
            } else {
                _uiState.update {
                    it.copy(
                        errorMessageRes = mensajeDeError(
                            error = result.exceptionOrNull(),
                            generico = R.string.error_eliminar_resena,
                        )
                    )
                }
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

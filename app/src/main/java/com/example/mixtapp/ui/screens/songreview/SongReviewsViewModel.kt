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

            // El album llega con todas sus resenas, y la mia ya viene marcada
            val result = albumRepository.getSongReviewById(songId = songId)
            val song = result.getOrNull()

            if (result.isSuccess && song != null) {
                _uiState.update {
                    it.copy(
                        // Mi resena va primero para que se vea apenas entro al album
                        song = song.copy(reviews = song.reviews.sortedByDescending { review -> review.esMia }),
                        miResenaId = song.reviews.find { review -> review.esMia }?.id,
                        isLoading = false,
                        errorMessageRes = null,
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessageRes = mensajeDeError(
                            error = result.exceptionOrNull(),
                            generico = R.string.error_cargar_contenido,
                        )
                    )
                }
            }
        }
    }

    // Quitar mi resena filtra la lista y recalcula el promedio, sin volver a pedir el album
    fun eliminarMiResena() {
        val song = _uiState.value.song ?: return
        val miResenaId = _uiState.value.miResenaId ?: return

        viewModelScope.launch {
            val result = reviewRepository.eliminarResena(reviewId = miResenaId)

            if (result.isSuccess) {
                val restantes = song.reviews.filter { it.id != miResenaId }

                _uiState.update {
                    it.copy(
                        song = albumRepository.conEstadisticas(album = song.album, reviews = restantes),
                        miResenaId = null,
                        errorMessageRes = null,
                    )
                }
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

package com.example.mixtapp.ui.screens.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.repository.AlbumRepository
import com.example.mixtapp.data.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Limite de caracteres de la resena
const val MAX_REVIEW_LENGTH = 500

const val MAX_DATE_LENGTH = 10

@HiltViewModel
class WriteReviewViewModel @Inject constructor(
    private val albumRepository: AlbumRepository,
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WriteReviewState())
    val uiState: StateFlow<WriteReviewState> = _uiState.asStateFlow()

    init {
        getDatosIniciales()
    }

    private fun getDatosIniciales() {
        viewModelScope.launch {
            val moods = reviewRepository.getMoods()
            val fecha = reviewRepository.getFechaEscuchaInicial()

            if (moods.isSuccess && fecha.isSuccess) {
                _uiState.update {
                    it.copy(
                        moods = moods.getOrNull() ?: emptyList(),
                        listenedDate = fecha.getOrNull() ?: "",
                    )
                }
            } else {
                _uiState.update { it.copy(errorMessageRes = R.string.error_cargar_contenido) }
            }
        }
    }

    // Buscar el album es responsabilidad del ViewModel, no de la navegacion
    fun getAlbumById(albumId: String) {
        if (_uiState.value.album != null) return

        viewModelScope.launch {
            val result = albumRepository.getAlbumById(albumId = albumId)

            if (result.isSuccess) {
                _uiState.update { it.copy(album = result.getOrNull()) }
            } else {
                _uiState.update { it.copy(errorMessageRes = R.string.error_cargar_contenido) }
            }
        }
    }

    fun updateRating(rating: Int) {
        val calificacionActual = _uiState.value.rating
        val nueva = if (rating == calificacionActual) 0 else rating

        _uiState.update { it.copy(rating = nueva) }
    }

    fun updateReviewText(reviewText: String) {
        _uiState.update {
            it.copy(reviewText = reviewText.take(MAX_REVIEW_LENGTH), errorMessageRes = null)
        }
    }

    fun seleccionarQuitarMood(mood: String) {
        val actuales = _uiState.value.selectedMoods
        val nuevos = if (mood in actuales) actuales - mood else actuales + mood

        _uiState.update { it.copy(selectedMoods = nuevos) }
    }

    fun updateListenedDate(listenedDate: String) {
        _uiState.update { it.copy(listenedDate = listenedDate.take(MAX_DATE_LENGTH)) }
    }

    fun usarFechaSugerida() {
        viewModelScope.launch {
            val result = reviewRepository.getFechaEscuchaSugerida()

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        listenedDate = result.getOrNull() ?: it.listenedDate,
                        errorMessageRes = null,
                    )
                }
            } else {
                _uiState.update { it.copy(errorMessageRes = R.string.error_fecha_sugerida) }
            }
        }
    }

    fun alternarFavorito() {
        val valorActual = _uiState.value.isFavorite
        _uiState.update { it.copy(isFavorite = !valorActual) }
    }

    fun publicarResena() {
        val estado = _uiState.value
        val album = estado.album ?: return
        val texto = estado.reviewText.trim()

        if (texto.isEmpty()) {
            _uiState.update { it.copy(errorMessageRes = R.string.error_resena_vacia) }
            return
        }

        viewModelScope.launch {
            val result = reviewRepository.publicarResena(
                album = album,
                rating = estado.rating,
                texto = texto,
                moods = estado.selectedMoods,
                fecha = estado.listenedDate,
            )

            if (result.isSuccess) {
                _uiState.update { it.copy(publicada = true, errorMessageRes = null) }
            } else {
                _uiState.update { it.copy(errorMessageRes = R.string.error_publicar_resena) }
            }
        }
    }
}

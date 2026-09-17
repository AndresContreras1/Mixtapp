package com.example.mixtapp.ui.screens.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
            }
        }
    }

    fun updateRating(rating: Int) {
        _uiState.update { it.copy(rating = rating) }
    }

    fun updateReviewText(reviewText: String) {
        _uiState.update { it.copy(reviewText = reviewText.take(MAX_REVIEW_LENGTH)) }
    }

    fun seleccionarQuitarMood(mood: String) {
        val actuales = _uiState.value.selectedMoods
        val nuevos = if (mood in actuales) actuales - mood else actuales + mood

        _uiState.update { it.copy(selectedMoods = nuevos) }
    }

    fun updateListenedDate(listenedDate: String) {
        _uiState.update { it.copy(listenedDate = listenedDate) }
    }

    fun usarFechaSugerida() {
        viewModelScope.launch {
            val result = reviewRepository.getFechaEscuchaSugerida()

            if (result.isSuccess) {
                _uiState.update { it.copy(listenedDate = result.getOrNull() ?: it.listenedDate) }
            }
        }
    }

    fun updateIsFavorite(isFavorite: Boolean) {
        _uiState.update { it.copy(isFavorite = isFavorite) }
    }
}

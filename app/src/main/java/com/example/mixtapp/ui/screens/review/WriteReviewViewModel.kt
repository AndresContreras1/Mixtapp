package com.example.mixtapp.ui.screens.review

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.mixtapp.data.local.LocalAlbumProvider
import com.example.mixtapp.data.local.LocalReviewAlbumProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

// Limite de caracteres de la resena
const val MAX_REVIEW_LENGTH = 500

@HiltViewModel
class WriteReviewViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(WriteReviewState())
    val uiState: StateFlow<WriteReviewState> = _uiState.asStateFlow()

    init {
        getDatosIniciales()
    }

    private fun getDatosIniciales() {
        _uiState.update {
            it.copy(
                moods = LocalReviewAlbumProvider.moods,
                listenedDate = LocalReviewAlbumProvider.fechaEscuchaInicial,
            )
        }
    }

    // Buscar el album es responsabilidad del ViewModel, no de la navegacion
    fun getAlbumById(albumId: String) {
        if (_uiState.value.album != null) return

        val album = LocalAlbumProvider.albums.find { it.id == albumId }

        _uiState.update { it.copy(album = album) }
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
        _uiState.update { it.copy(listenedDate = LocalReviewAlbumProvider.fechaEscuchaSugerida) }
    }

    fun updateIsFavorite(isFavorite: Boolean) {
        _uiState.update { it.copy(isFavorite = isFavorite) }
    }
}

package com.example.mixtapp.ui.screens.review

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.mixtapp.data.local.LocalReviewAlbumProvider
import com.example.mixtapp.ui.screens.review.model.ReviewDraftUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

// Limite de caracteres de la resena
const val MaxReviewLength = 500

@HiltViewModel
class WriteReviewViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(WriteReviewState())
    val uiState: StateFlow<WriteReviewState> = _uiState.asStateFlow()

    // Buscar el album es responsabilidad del ViewModel, no de la navegacion
    fun getAlbumById(albumId: String) {
        val album = LocalReviewAlbumProvider.albums.find { it.id == albumId }

        _uiState.update { it.copy(album = album) }
    }

    fun updateRating(rating: Int) {
        _uiState.update { it.copy(rating = rating) }
    }

    fun updateReviewText(reviewText: String) {
        _uiState.update { it.copy(reviewText = reviewText.take(MaxReviewLength)) }
    }

    fun seleccionarQuitarMood(mood: String) {
        val actuales = _uiState.value.selectedMoods
        val nuevos = if (mood in actuales) actuales - mood else actuales + mood

        _uiState.update { it.copy(selectedMoods = nuevos) }
    }

    fun updateListenedDate(listenedDate: String) {
        _uiState.update { it.copy(listenedDate = listenedDate) }
    }

    fun updateIsFavorite(isFavorite: Boolean) {
        _uiState.update { it.copy(isFavorite = isFavorite) }
    }

    fun publicarResena() {
        _uiState.update { it.copy(hasPosted = true) }
    }

    // Arma el borrador con lo que hay ahora mismo en el estado
    fun crearBorrador(): ReviewDraftUi {
        val estado = _uiState.value

        return ReviewDraftUi(
            rating = estado.rating,
            review = estado.reviewText,
            moods = estado.selectedMoods,
            listenedDate = estado.listenedDate,
            isFavorite = estado.isFavorite,
        )
    }
}

// Moods disponibles al escribir una resena
val defaultReviewMoods = listOf(
    "Melancholic",
    "Nostalgic",
    "Intense",
    "Chill",
    "Romantic",
    "Energetic",
    "Ethereal",
    "Playful",
)

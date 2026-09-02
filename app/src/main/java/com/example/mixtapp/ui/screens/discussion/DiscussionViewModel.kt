package com.example.mixtapp.ui.screens.discussion

import androidx.lifecycle.ViewModel
import com.example.mixtapp.data.local.LocalDiscussionProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DiscussionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DiscussionState())
    val uiState: StateFlow<DiscussionState> = _uiState.asStateFlow()

    // Buscar la discusion es responsabilidad del ViewModel, no de la navegacion
    fun getDiscussionByReviewId(reviewId: String) {
        val discussion = LocalDiscussionProvider.discussions.find { it.review.id == reviewId }

        _uiState.update {
            it.copy(
                discussion = discussion,
                isReviewLiked = discussion?.review?.isLiked ?: false,
                isReviewShared = discussion?.review?.isShared ?: false,
                likedCommentIds = discussion?.comments
                    ?.filter { comentario -> comentario.isLiked }
                    ?.map { comentario -> comentario.id }
                    ?.toSet()
                    ?: emptySet(),
            )
        }
    }

    fun darQuitarLikeResena() {
        val valorActual = _uiState.value.isReviewLiked
        _uiState.update { it.copy(isReviewLiked = !valorActual) }
    }

    fun compartirQuitarResena() {
        val valorActual = _uiState.value.isReviewShared
        _uiState.update { it.copy(isReviewShared = !valorActual) }
    }

    fun darQuitarLikeComentario(commentId: String) {
        val actuales = _uiState.value.likedCommentIds
        val nuevos = if (commentId in actuales) actuales - commentId else actuales + commentId

        _uiState.update { it.copy(likedCommentIds = nuevos) }
    }

    fun responderComentario(commentId: String) {
        _uiState.update { it.copy(replyingToCommentId = commentId) }
    }
}

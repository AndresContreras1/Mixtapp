package com.example.mixtapp.ui.screens.discussion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.data.model.DiscussionUi
import com.example.mixtapp.data.repository.AuthRepository
import com.example.mixtapp.data.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscussionViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiscussionState())
    val uiState: StateFlow<DiscussionState> = _uiState.asStateFlow()

    // Buscar la discusion es responsabilidad del ViewModel, no de la navegacion
    fun getDiscussionByReviewId(reviewId: String) {
        if (_uiState.value.discussion != null) return

        viewModelScope.launch {
            val result = reviewRepository.getDiscussionByReviewId(reviewId = reviewId)

            if (result.isSuccess) {
                val discussion = result.getOrNull()

                _uiState.update {
                    it.copy(
                        discussion = discussion,
                        comentarios = discussion?.comments ?: emptyList(),
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
        }
    }

    fun updateNuevoComentario(texto: String) {
        _uiState.update { it.copy(nuevoComentario = texto) }
    }

    fun publicarComentario() {
        val estado = _uiState.value
        val reviewId = estado.discussion?.id ?: return
        val texto = estado.nuevoComentario.trim()

        if (texto.isEmpty()) return

        val autor = authRepository.currentUser?.email?.substringBefore("@") ?: ""

        viewModelScope.launch {
            val result = reviewRepository.publicarComentario(
                reviewId = reviewId,
                autor = autor,
                texto = texto,
            )

            if (result.isSuccess) {
                actualizarDiscusion(discussion = result.getOrNull())
                _uiState.update { it.copy(nuevoComentario = "") }
            }
        }
    }

    fun darQuitarLikeResena() {
        val reviewId = _uiState.value.discussion?.id ?: return

        viewModelScope.launch {
            val result = reviewRepository.darQuitarLikeResena(reviewId = reviewId)

            if (result.isSuccess) {
                actualizarDiscusion(discussion = result.getOrNull())
            }
        }
    }

    fun compartirQuitarResena() {
        val reviewId = _uiState.value.discussion?.id ?: return

        viewModelScope.launch {
            val result = reviewRepository.compartirQuitarResena(reviewId = reviewId)

            if (result.isSuccess) {
                actualizarDiscusion(discussion = result.getOrNull())
            }
        }
    }

    fun darQuitarLikeComentario(commentId: String) {
        val reviewId = _uiState.value.discussion?.id ?: return

        viewModelScope.launch {
            val result = reviewRepository.darQuitarLikeComentario(reviewId = reviewId, commentId = commentId)

            if (result.isSuccess) {
                actualizarDiscusion(discussion = result.getOrNull())
            }
        }
    }

    private fun actualizarDiscusion(discussion: DiscussionUi?) {
        if (discussion == null) return

        _uiState.update {
            it.copy(
                discussion = discussion,
                comentarios = discussion.comments,
                isReviewLiked = discussion.review.isLiked,
                isReviewShared = discussion.review.isShared,
                likedCommentIds = discussion.comments
                    .filter { comentario -> comentario.isLiked }
                    .map { comentario -> comentario.id }
                    .toSet(),
            )
        }
    }

    fun responderComentario(commentId: String) {
        _uiState.update { it.copy(replyingToCommentId = commentId) }
    }
}

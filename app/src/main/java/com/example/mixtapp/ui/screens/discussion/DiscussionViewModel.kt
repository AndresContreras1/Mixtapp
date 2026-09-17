package com.example.mixtapp.ui.screens.discussion

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.mixtapp.data.local.LocalDiscussionProvider
import com.example.mixtapp.data.model.DiscussionCommentUi
import com.example.mixtapp.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class DiscussionViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiscussionState())
    val uiState: StateFlow<DiscussionState> = _uiState.asStateFlow()

    // Buscar la discusion es responsabilidad del ViewModel, no de la navegacion
    fun getDiscussionByReviewId(reviewId: String) {
        if (_uiState.value.discussion != null) return

        val discussion = LocalDiscussionProvider.discussions.find { it.id == reviewId }

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

    fun updateNuevoComentario(texto: String) {
        _uiState.update { it.copy(nuevoComentario = texto) }
    }

    fun publicarComentario() {
        val estado = _uiState.value
        val texto = estado.nuevoComentario.trim()

        if (texto.isEmpty()) return

        val autor = authRepository.currentUser?.email?.substringBefore("@") ?: ""

        val comentario = DiscussionCommentUi(
            id = "comentario-propio-" + (estado.comentarios.size + 1),
            author = autor,
            initials = autor.take(2).lowercase(),
            timeAgo = "ahora",
            content = texto,
            likes = 0,
            isReply = false,
            isLiked = false,
        )

        _uiState.update {
            it.copy(
                comentarios = it.comentarios + comentario,
                nuevoComentario = "",
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

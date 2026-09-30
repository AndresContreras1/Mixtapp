package com.example.mixtapp.ui.screens.discussion

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.model.DiscussionUi
import com.example.mixtapp.data.repository.AuthRepository
import com.example.mixtapp.data.repository.ContenidoNoEncontradoException
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
                _uiState.update {
                    it.copy(discussion = result.getOrNull(), errorMessageRes = null)
                }
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

    fun updateNuevoComentario(texto: String) {
        _uiState.update { it.copy(nuevoComentario = texto, errorMessageRes = null) }
    }

    fun publicarComentario() {
        val reviewId = _uiState.value.discussion?.id ?: return
        val texto = _uiState.value.nuevoComentario.trim()

        if (texto.isEmpty()) {
            _uiState.update { it.copy(errorMessageRes = R.string.error_comentario_vacio) }
            return
        }

        val autor = authRepository.currentUser?.email?.substringBefore("@") ?: ""

        viewModelScope.launch {
            val result = reviewRepository.publicarComentario(
                reviewId = reviewId,
                autor = autor,
                texto = texto,
            )

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        discussion = result.getOrNull(),
                        nuevoComentario = "",
                        errorMessageRes = null,
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        errorMessageRes = mensajeDeError(
                            error = result.exceptionOrNull(),
                            generico = R.string.error_publicar_comentario,
                        )
                    )
                }
            }
        }
    }

    fun darQuitarLikeResena() {
        val reviewId = _uiState.value.discussion?.id ?: return

        viewModelScope.launch {
            val result = reviewRepository.darQuitarLikeResena(reviewId = reviewId)

            actualizarDesde(result = result, generico = R.string.error_me_gusta)
        }
    }

    fun compartirQuitarResena() {
        val reviewId = _uiState.value.discussion?.id ?: return

        viewModelScope.launch {
            val result = reviewRepository.compartirQuitarResena(reviewId = reviewId)

            actualizarDesde(result = result, generico = R.string.error_compartir)
        }
    }

    fun darQuitarLikeComentario(commentId: String) {
        val reviewId = _uiState.value.discussion?.id ?: return

        viewModelScope.launch {
            val result = reviewRepository.darQuitarLikeComentario(
                reviewId = reviewId,
                commentId = commentId,
            )

            actualizarDesde(result = result, generico = R.string.error_me_gusta)
        }
    }

    fun responderComentario(commentId: String) {
        _uiState.update { it.copy(replyingToCommentId = commentId) }
    }

    private fun actualizarDesde(result: Result<DiscussionUi>, @StringRes generico: Int) {
        if (result.isSuccess) {
            _uiState.update { it.copy(discussion = result.getOrNull(), errorMessageRes = null) }
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
        else -> generico
    }
}

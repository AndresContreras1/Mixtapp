package com.example.mixtapp.ui.screens.discussion

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.model.DiscussionCommentUi
import com.example.mixtapp.data.repository.AuthRepository
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
            _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }

            val result = reviewRepository.getDiscussionByReviewId(reviewId = reviewId)

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(discussion = result.getOrNull(), isLoading = false, errorMessageRes = null)
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

        val autor = authRepository.usuarioDelCorreo

        viewModelScope.launch {
            val result = reviewRepository.publicarComentario(
                reviewId = reviewId,
                autor = autor,
                texto = texto,
                esRespuesta = _uiState.value.replyingToCommentId != null,
            )

            if (result.isSuccess) {
                actualizarComentarios(comentarios = result.getOrNull() ?: emptyList())
                _uiState.update { it.copy(nuevoComentario = "", replyingToCommentId = null) }
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

    // Si el like sale bien se invierte el corazon y el contador sube o baja, sin volver a pedir
    // la resena
    fun sendOrDeleteLike() {
        val reviewId = _uiState.value.discussion?.id ?: return

        viewModelScope.launch {
            val result = reviewRepository.sendOrDeleteLike(reviewId = reviewId)

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        discussion = it.discussion?.copy(
                            review = it.discussion.review.copy(
                                isLiked = !it.discussion.review.isLiked,
                                likes = if (it.discussion.review.isLiked) it.discussion.review.likes - 1 else it.discussion.review.likes + 1,
                            )
                        ),
                        errorMessageRes = null,
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        errorMessageRes = mensajeDeError(
                            error = result.exceptionOrNull(),
                            generico = R.string.error_me_gusta,
                        )
                    )
                }
            }
        }
    }

    // Compartir solo marca la tarjeta en esta pantalla
    fun compartirQuitarResena() {
        _uiState.update {
            it.copy(
                discussion = it.discussion?.copy(
                    review = it.discussion.review.copy(isShared = !it.discussion.review.isShared)
                )
            )
        }
    }

    fun darQuitarLikeComentario(commentId: String) {
        val reviewId = _uiState.value.discussion?.id ?: return

        viewModelScope.launch {
            val result = reviewRepository.darQuitarLikeComentario(
                reviewId = reviewId,
                commentId = commentId,
            )

            if (result.isSuccess) {
                actualizarComentarios(comentarios = result.getOrNull() ?: emptyList())
            } else {
                _uiState.update {
                    it.copy(
                        errorMessageRes = mensajeDeError(
                            error = result.exceptionOrNull(),
                            generico = R.string.error_me_gusta,
                        )
                    )
                }
            }
        }
    }

    fun responderComentario(commentId: String) {
        _uiState.update { it.copy(replyingToCommentId = commentId) }
    }

    // El total del encabezado cambia con la lista
    private fun actualizarComentarios(comentarios: List<DiscussionCommentUi>) {
        _uiState.update {
            it.copy(
                discussion = it.discussion?.copy(
                    review = it.discussion.review.copy(commentsCount = comentarios.size),
                    comments = comentarios,
                ),
                errorMessageRes = null,
            )
        }
    }

    @StringRes
    private fun mensajeDeError(error: Throwable?, @StringRes generico: Int): Int = when (error) {
        is ContenidoNoEncontradoException -> R.string.discusion_no_encontrada
        is ErrorDelServidorException -> R.string.error_servidor
        is SinConexionException -> R.string.error_sin_conexion
        else -> generico
    }
}

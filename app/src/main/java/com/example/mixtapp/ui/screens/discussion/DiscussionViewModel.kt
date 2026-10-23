package com.example.mixtapp.ui.screens.discussion

import android.util.Log
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
import kotlinx.coroutines.flow.catch
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

    // El flujo se escucha mientras exista el ViewModel: si se volviera a pedir cada vez que la
    // pantalla se muestra, se acumularian listeners
    private var escuchandoDiscusion = false

    // Datos en vivo: un like desde otro celular cambia el corazon y el contador sin salir
    fun getDiscussionByReviewId(reviewId: String) {
        if (escuchandoDiscusion) return
        escuchandoDiscusion = true

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }

            reviewRepository.getDiscussionLive(reviewId = reviewId)
                .catch { e ->
                    Log.d("DiscussionViewModel", "getDiscussionByReviewId: ${e.message}")
                    escuchandoDiscusion = false
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessageRes = mensajeDeError(error = e, generico = R.string.error_cargar_contenido),
                        )
                    }
                }
                .collect { discussion ->
                    _uiState.update {
                        it.copy(
                            // Compartir solo existe en esta pantalla: se conserva al llegar datos nuevos
                            discussion = discussion.copy(
                                review = discussion.review.copy(isShared = it.discussion?.review?.isShared ?: false)
                            ),
                            isLoading = false,
                            errorMessageRes = null,
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

    // No se copia el estado a mano: la transaccion cambia likesCount, eso dispara el flujo y llega
    // la resena con el corazon y el contador al dia
    fun sendOrDeleteLike() {
        val reviewId = _uiState.value.discussion?.id ?: return

        viewModelScope.launch {
            val result = reviewRepository.sendOrDeleteLike(reviewId = reviewId)

            if (result.isSuccess) {
                _uiState.update { it.copy(errorMessageRes = null) }
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

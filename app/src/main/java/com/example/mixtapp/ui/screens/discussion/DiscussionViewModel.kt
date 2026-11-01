package com.example.mixtapp.ui.screens.discussion

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.injection.IoDispatcher
import com.example.mixtapp.data.repository.ReviewRepository
import com.example.mixtapp.ui.utils.mensajeDeError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscussionViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository,
    // Las corrutinas se lanzan en este hilo; las pruebas pasan uno de prueba
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
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

        viewModelScope.launch(ioDispatcher) {
            _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }

            reviewRepository.getDiscussionLive(reviewId = reviewId)
                .catch { e ->
                    Log.d("DiscussionViewModel", "getDiscussionByReviewId: ${e.message}")
                    escuchandoDiscusion = false
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessageRes = mensajeDeError(error = e, generico = R.string.error_cargar_contenido, noEncontrado = R.string.discusion_no_encontrada),
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

    // No se agrega el comentario a mano: se guarda en Firestore y el flujo trae la lista al dia
    fun publicarComentario() {
        val discussion = _uiState.value.discussion ?: return
        val texto = _uiState.value.nuevoComentario.trim()

        if (texto.isEmpty()) {
            _uiState.update { it.copy(errorMessageRes = R.string.error_comentario_vacio) }
            return
        }

        // Si se responde a una respuesta, la nueva queda en el hilo del comentario principal
        val respondiendoA = discussion.comments.find { it.id == _uiState.value.replyingToCommentId }
        val parentCommentId = respondiendoA?.parentId ?: respondiendoA?.id

        viewModelScope.launch(ioDispatcher) {
            val result = reviewRepository.publicarComentario(
                reviewId = discussion.id,
                texto = texto,
                parentCommentId = parentCommentId,
            )

            if (result.isSuccess) {
                _uiState.update { it.copy(nuevoComentario = "", replyingToCommentId = null, errorMessageRes = null) }
            } else {
                _uiState.update {
                    it.copy(
                        errorMessageRes = mensajeDeError(
                            error = result.exceptionOrNull(),
                            generico = R.string.error_publicar_comentario, noEncontrado = R.string.discusion_no_encontrada,
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

        viewModelScope.launch(ioDispatcher) {
            val result = reviewRepository.sendOrDeleteLike(reviewId = reviewId)

            if (result.isSuccess) {
                _uiState.update { it.copy(errorMessageRes = null) }
            } else {
                _uiState.update {
                    it.copy(
                        errorMessageRes = mensajeDeError(
                            error = result.exceptionOrNull(),
                            generico = R.string.error_me_gusta, noEncontrado = R.string.discusion_no_encontrada,
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

    // Igual que el like de la resena: la transaccion cambia likesCount del comentario y el flujo
    // trae el corazon y el contador al dia
    fun sendOrDeleteLikeComentario(commentId: String) {
        val reviewId = _uiState.value.discussion?.id ?: return

        viewModelScope.launch(ioDispatcher) {
            val result = reviewRepository.sendOrDeleteLikeComentario(
                reviewId = reviewId,
                commentId = commentId,
            )

            if (result.isSuccess) {
                _uiState.update { it.copy(errorMessageRes = null) }
            } else {
                _uiState.update {
                    it.copy(
                        errorMessageRes = mensajeDeError(
                            error = result.exceptionOrNull(),
                            generico = R.string.error_me_gusta, noEncontrado = R.string.discusion_no_encontrada,
                        )
                    )
                }
            }
        }
    }

    fun responderComentario(commentId: String) {
        _uiState.update { it.copy(replyingToCommentId = commentId) }
    }

}

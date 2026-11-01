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

    // Datos en vivo: un comentario nuevo o un like desde otro celular aparecen sin salir
    fun getDiscussionByReviewId(reviewId: String) {
        if (escuchandoDiscusion) return
        escuchandoDiscusion = true

        viewModelScope.launch(ioDispatcher) {
            _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }

            reviewRepository.getDiscussionLive(reviewId = reviewId)
                .catch { e ->
                    Log.d("DiscussionViewModel", "getDiscussionByReviewId: ${e.message}")
                    // Si se cerro con error se puede volver a escuchar al entrar de nuevo
                    escuchandoDiscusion = false
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessageRes = mensajeDeError(
                                error = e,
                                generico = R.string.error_cargar_contenido,
                                noEncontrado = R.string.discusion_no_encontrada,
                            ),
                        )
                    }
                }
                .collect { discussion ->
                    _uiState.update {
                        it.copy(discussion = discussion, isLoading = false, errorMessageRes = null)
                    }
                }
        }
    }

    // Lo que se va escribiendo en el campo de abajo
    fun updateNuevoComentario(texto: String) {
        _uiState.update { it.copy(nuevoComentario = texto, errorAccionRes = null) }
    }

    // No se agrega el comentario a mano: se guarda en Firestore y el flujo trae la lista al dia
    fun publicarComentario() {
        val estado = _uiState.value
        val discussion = estado.discussion ?: return
        val texto = estado.nuevoComentario.trim()

        if (estado.publicando) return

        if (texto.isEmpty()) {
            _uiState.update { it.copy(errorAccionRes = R.string.error_comentario_vacio) }
            return
        }

        // Si se responde a una respuesta, la nueva queda en el hilo del comentario principal
        val respondiendoA = discussion.comments.find { it.id == estado.replyingToCommentId }
        val parentCommentId = respondiendoA?.parentId ?: respondiendoA?.id

        viewModelScope.launch(ioDispatcher) {
            _uiState.update { it.copy(publicando = true, errorAccionRes = null) }

            val result = reviewRepository.publicarComentario(
                reviewId = discussion.id,
                texto = texto,
                parentCommentId = parentCommentId,
            )

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(nuevoComentario = "", replyingToCommentId = null, publicando = false, errorAccionRes = null)
                }
            } else {
                _uiState.update {
                    it.copy(
                        publicando = false,
                        errorAccionRes = mensajeDeError(
                            error = result.exceptionOrNull(),
                            generico = R.string.error_publicar_comentario,
                            noEncontrado = R.string.discusion_no_encontrada,
                        )
                    )
                }
            }
        }
    }

    // No se copia el estado a mano: la transaccion cambia likesCount, eso dispara el flujo y llega
    // la resena con el corazon y el contador al dia
    fun sendOrDeleteLike(reviewId: String) {
        viewModelScope.launch(ioDispatcher) {
            val result = reviewRepository.sendOrDeleteLike(reviewId = reviewId)

            if (result.isSuccess) {
                _uiState.update { it.copy(errorAccionRes = null) }
            } else {
                _uiState.update {
                    it.copy(
                        errorAccionRes = mensajeDeError(
                            error = result.exceptionOrNull(),
                            generico = R.string.error_me_gusta,
                            noEncontrado = R.string.discusion_no_encontrada,
                        )
                    )
                }
            }
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
                _uiState.update { it.copy(errorAccionRes = null) }
            } else {
                _uiState.update {
                    it.copy(
                        errorAccionRes = mensajeDeError(
                            error = result.exceptionOrNull(),
                            generico = R.string.error_me_gusta,
                            noEncontrado = R.string.discusion_no_encontrada,
                        )
                    )
                }
            }
        }
    }

    // El siguiente comentario que se publique sera la respuesta a este
    fun responderComentario(commentId: String) {
        _uiState.update { it.copy(replyingToCommentId = commentId) }
    }

    // La X de "Respondiendo a": el siguiente comentario vuelve a ser para la resena
    fun cancelarRespuesta() {
        _uiState.update { it.copy(replyingToCommentId = null) }
    }

}

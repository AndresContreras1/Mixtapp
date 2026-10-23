package com.example.mixtapp.ui.screens.albumdetail

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.repository.AlbumRepository
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
class AlbumDetailViewModel @Inject constructor(
    private val albumRepository: AlbumRepository,
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private var albumSubscription: kotlinx.coroutines.Job? = null

    private val _uiState = MutableStateFlow(AlbumDetailState())
    val uiState: StateFlow<AlbumDetailState> = _uiState.asStateFlow()

    // Buscar el album es responsabilidad del ViewModel, no de la navegacion
    fun getAlbumById(albumId: String) {
        albumSubscription?.cancel()
        albumSubscription = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }

            // El album llega con todas sus resenas, y la mia ya viene marcada
            albumRepository.observeAlbum(albumId).collect { result ->
                val albumReviews = result.getOrNull()

                if (result.isSuccess && albumReviews != null) {
                    _uiState.update {
                        it.copy(
                            // De la escuchada mas reciente a la mas antigua, y mi resena primero
                            // para que se vea apenas entro al album
                            albumReviews = albumReviews.copy(
                                reviews = albumReviews.reviews
                                .sortedByDescending { review -> review.fecha }
                                .sortedByDescending { review -> review.esMia }
                            ),
                            miResenaId = albumReviews.reviews.find { review -> review.esMia }?.id,
                            isLoading = false,
                            errorMessageRes = null,
                        )
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
    }

    // Quitar mi resena filtra la lista y recalcula el promedio, sin volver a pedir el album
    fun eliminarMiResena() {
        val albumReviews = _uiState.value.albumReviews ?: return
        val miResenaId = _uiState.value.miResenaId ?: return

        if (_uiState.value.eliminando) return

        viewModelScope.launch {
            _uiState.update { it.copy(eliminando = true, errorMessageRes = null) }

            val result = reviewRepository.eliminarResena(reviewId = miResenaId)

            if (result.isSuccess) {
                // Se filtra la lista que hay ahora en el estado
                val restantes = (_uiState.value.albumReviews?.reviews ?: emptyList()).filter { it.id != miResenaId }

                _uiState.update {
                    it.copy(
                        albumReviews = albumRepository.conEstadisticas(album = albumReviews.album, reviews = restantes),
                        miResenaId = null,
                        eliminando = false,
                        errorMessageRes = null,
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        eliminando = false,
                        errorMessageRes = mensajeDeError(
                            error = result.exceptionOrNull(),
                            generico = R.string.error_eliminar_resena,
                        )
                    )
                }
            }
        }
    }

    // Firestore confirma el like; la suscripcion actualiza el corazon y el contador.
    fun sendOrDeleteLike(reviewId: String) {
        viewModelScope.launch {
            val result = reviewRepository.sendOrDeleteLike(reviewId = reviewId)

            if (result.isSuccess) {
                // El listener aplica el estado confirmado sin duplicar contadores.
            } else {
                _uiState.update {
                    it.copy(
                        errorMessageRes = mensajeDeError(
                            error = result.exceptionOrNull(),
                            generico = R.string.error_actualizar_like,
                        )
                    )
                }
            }
        }
    }

    @StringRes
    private fun mensajeDeError(error: Throwable?, @StringRes generico: Int): Int = when (error) {
        is ContenidoNoEncontradoException -> R.string.contenido_no_encontrado
        is ErrorDelServidorException -> R.string.error_servidor
        is SinConexionException -> R.string.error_sin_conexion
        else -> generico
    }
}

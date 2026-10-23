package com.example.mixtapp.ui.screens.songreview

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
class SongReviewsViewModel @Inject constructor(
    private val albumRepository: AlbumRepository,
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SongReviewsState())
    val uiState: StateFlow<SongReviewsState> = _uiState.asStateFlow()

    // Buscar el album es responsabilidad del ViewModel, no de la navegacion
    fun getSongById(songId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }

            // El album llega con todas sus resenas, y la mia ya viene marcada
            val result = albumRepository.getSongReviewById(songId = songId)
            val song = result.getOrNull()

            if (result.isSuccess && song != null) {
                _uiState.update {
                    it.copy(
                        // De la escuchada mas reciente a la mas antigua, y mi resena primero
                        // para que se vea apenas entro al album
                        song = song.copy(
                            reviews = song.reviews
                                .sortedByDescending { review -> review.fecha }
                                .sortedByDescending { review -> review.esMia }
                        ),
                        miResenaId = song.reviews.find { review -> review.esMia }?.id,
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

    // Quitar mi resena filtra la lista y recalcula el promedio, sin volver a pedir el album
    fun eliminarMiResena() {
        val song = _uiState.value.song ?: return
        val miResenaId = _uiState.value.miResenaId ?: return

        if (_uiState.value.eliminando) return

        viewModelScope.launch {
            _uiState.update { it.copy(eliminando = true, errorMessageRes = null) }

            val result = reviewRepository.eliminarResena(reviewId = miResenaId)

            if (result.isSuccess) {
                // Se filtra la lista que hay ahora en el estado
                val restantes = (_uiState.value.song?.reviews ?: emptyList()).filter { it.id != miResenaId }

                _uiState.update {
                    it.copy(
                        song = albumRepository.conEstadisticas(album = song.album, reviews = restantes),
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

    fun darQuitarLike(reviewId: String) {
        val estado = _uiState.value
        val review = estado.song?.reviews?.find { it.id == reviewId } ?: return
        if (reviewId in estado.likesPendientes) return
        val darLike = !review.isLiked
        _uiState.update { it.copy(likesPendientes = it.likesPendientes + reviewId, errorMessageRes = null) }
        viewModelScope.launch {
            val result = reviewRepository.actualizarLike(reviewId, darLike)
            _uiState.update { actual ->
                actual.copy(
                    likesPendientes = actual.likesPendientes - reviewId,
                    song = actual.song?.copy(reviews = actual.song.reviews.map {
                        if (it.id == reviewId && result.isSuccess) {
                            it.copy(likes = result.getOrThrow(), isLiked = darLike)
                        } else it
                    }),
                    errorMessageRes = if (result.isFailure) mensajeDeError(
                        result.exceptionOrNull(), R.string.error_actualizar_like
                    ) else null,
                )
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

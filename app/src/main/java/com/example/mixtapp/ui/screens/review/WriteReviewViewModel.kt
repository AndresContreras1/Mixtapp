package com.example.mixtapp.ui.screens.review

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.repository.AlbumRepository
import com.example.mixtapp.data.repository.ContenidoNoEncontradoException
import com.example.mixtapp.data.repository.ErrorDelServidorException
import com.example.mixtapp.data.repository.SinConexionException
import com.example.mixtapp.data.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Limite de caracteres de la resena
const val MAX_REVIEW_LENGTH = 500

const val MAX_DATE_LENGTH = 10

@HiltViewModel
class WriteReviewViewModel @Inject constructor(
    private val albumRepository: AlbumRepository,
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WriteReviewState())
    val uiState: StateFlow<WriteReviewState> = _uiState.asStateFlow()

    init {
        getDatosIniciales()
    }

    private fun getDatosIniciales() {
        viewModelScope.launch {
            // Las dos peticiones salen a la vez y se espera la mas lenta
            val moodsPendientes = async { reviewRepository.getMoods() }
            val fechaPendiente = async { reviewRepository.getFechaEscuchaInicial() }

            val moods = moodsPendientes.await()
            val fecha = fechaPendiente.await()

            if (moods.isSuccess && fecha.isSuccess) {
                _uiState.update {
                    it.copy(
                        moods = moods.getOrNull() ?: emptyList(),
                        listenedDate = fecha.getOrNull() ?: "",
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        errorMessageRes = mensajeDeError(
                            error = moods.exceptionOrNull() ?: fecha.exceptionOrNull(),
                            generico = R.string.error_cargar_contenido,
                        )
                    )
                }
            }
        }
    }

    // Buscar el album es responsabilidad del ViewModel, no de la navegacion
    fun getAlbumById(albumId: String) {
        if (_uiState.value.album != null) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }
            // El album y la resena que ya existe salen a la vez
            val albumPendiente = async { albumRepository.getAlbumById(albumId = albumId) }
            val resenaPendiente = async { reviewRepository.getMyReviewByAlbumId(albumId = albumId) }

            val album = albumPendiente.await()
            val resena = resenaPendiente.await()

            if (album.isSuccess && resena.isSuccess) {
                // Solo se permite una calificacion por album: si ya existe se edita
                val yaCalificado = resena.getOrNull()
                val estado = _uiState.value

                _uiState.update {
                    it.copy(
                        album = album.getOrNull(),
                        rating = yaCalificado?.rating ?: estado.rating,
                        reviewText = yaCalificado?.excerpt ?: estado.reviewText,
                        selectedMoods = yaCalificado?.tags ?: estado.selectedMoods,
                        listenedDate = yaCalificado?.date ?: estado.listenedDate,
                        isLoading = false,
                        errorMessageRes = null,
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessageRes = mensajeDeError(
                            error = album.exceptionOrNull() ?: resena.exceptionOrNull(),
                            generico = R.string.error_cargar_contenido,
                        )
                    )
                }
            }
        }
    }

    fun updateRating(rating: Int) {
        val calificacionActual = _uiState.value.rating
        val nueva = if (rating == calificacionActual) 0 else rating

        _uiState.update { it.copy(rating = nueva) }
    }

    fun updateReviewText(reviewText: String) {
        _uiState.update {
            it.copy(reviewText = reviewText.take(MAX_REVIEW_LENGTH), errorMessageRes = null)
        }
    }

    fun seleccionarQuitarMood(mood: String) {
        val actuales = _uiState.value.selectedMoods
        val nuevos = if (mood in actuales) actuales - mood else actuales + mood

        _uiState.update { it.copy(selectedMoods = nuevos) }
    }

    fun updateListenedDate(listenedDate: String) {
        _uiState.update { it.copy(listenedDate = listenedDate.take(MAX_DATE_LENGTH)) }
    }

    fun usarFechaSugerida() {
        val fechaActual = _uiState.value.listenedDate

        viewModelScope.launch {
            val result = reviewRepository.getFechaEscuchaSugerida()

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        listenedDate = result.getOrNull() ?: fechaActual,
                        errorMessageRes = null,
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        errorMessageRes = mensajeDeError(
                            error = result.exceptionOrNull(),
                            generico = R.string.error_fecha_sugerida,
                        )
                    )
                }
            }
        }
    }

    fun alternarFavorito() {
        val valorActual = _uiState.value.isFavorite

        _uiState.update { it.copy(isFavorite = !valorActual) }
    }

    fun publicarResena() {
        val estado = _uiState.value
        val album = estado.album ?: return
        val texto = estado.reviewText.trim()

        if (texto.isEmpty()) {
            _uiState.update { it.copy(errorMessageRes = R.string.error_resena_vacia) }
            return
        }

        viewModelScope.launch {
            val result = reviewRepository.publicarResena(
                album = album,
                rating = estado.rating,
                texto = texto,
                moods = estado.selectedMoods,
                fecha = estado.listenedDate,
            )

            if (result.isSuccess) {
                _uiState.update { it.copy(publicada = true, errorMessageRes = null) }
            } else {
                _uiState.update {
                    it.copy(
                        errorMessageRes = mensajeDeError(
                            error = result.exceptionOrNull(),
                            generico = R.string.error_publicar_resena,
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

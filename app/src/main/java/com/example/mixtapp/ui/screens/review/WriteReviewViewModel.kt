package com.example.mixtapp.ui.screens.review

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.model.MAX_DATE_LENGTH
import com.example.mixtapp.data.model.MAX_REVIEW_LENGTH
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
class WriteReviewViewModel @Inject constructor(
    private val albumRepository: AlbumRepository,
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WriteReviewState())
    val uiState: StateFlow<WriteReviewState> = _uiState.asStateFlow()

    // Buscar el album es responsabilidad del ViewModel, no de la navegacion
    fun getAlbumById(albumId: String) {
        if (_uiState.value.album != null) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }

            // El album llega con sus resenas, y entre ellas la mia si ya lo califique
            val result = albumRepository.getSongReviewById(songId = albumId)
            val song = result.getOrNull()

            if (result.isSuccess && song != null) {
                // Solo se permite una calificacion por album: si ya existe se edita
                val yaCalificado = song.reviews.find { it.esMia }

                _uiState.update {
                    it.copy(
                        album = song.album,
                        reviewId = yaCalificado?.id,
                        rating = yaCalificado?.rating ?: 0,
                        reviewText = yaCalificado?.content ?: "",
                        listenedDate = yaCalificado?.fecha ?: "",
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

    fun updateListenedDate(listenedDate: String) {
        _uiState.update {
            it.copy(listenedDate = listenedDate.take(MAX_DATE_LENGTH), errorMessageRes = null)
        }
    }

    fun publicarResena() {
        val estado = _uiState.value
        val album = estado.album ?: return
        val texto = estado.reviewText.trim()
        val fecha = estado.listenedDate.trim()

        if (estado.publicando) return

        val errorRes = when {
            texto.isEmpty() -> R.string.error_resena_vacia
            !esFechaValida(fecha = fecha) -> R.string.error_fecha_invalida
            else -> null
        }

        // Si el formulario no pasa las validaciones no se consulta la base
        if (errorRes != null) {
            _uiState.update { it.copy(errorMessageRes = errorRes) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(publicando = true, errorMessageRes = null) }

            val result = reviewRepository.guardarResena(
                albumId = album.id,
                rating = estado.rating,
                texto = texto,
                fecha = fecha,
                reviewId = estado.reviewId,
            )

            if (result.isSuccess) {
                _uiState.update { it.copy(publicando = false, publicada = true, errorMessageRes = null) }
            } else {
                _uiState.update {
                    it.copy(
                        publicando = false,
                        errorMessageRes = mensajeDeError(
                            error = result.exceptionOrNull(),
                            generico = R.string.error_publicar_resena,
                        )
                    )
                }
            }
        }
    }

    // La fecha es opcional; si se escribe, tiene que ser AAAA-MM-DD con mes y dia posibles
    private fun esFechaValida(fecha: String): Boolean {
        if (fecha.isEmpty()) return true

        val formatoCorrecto = fecha.length == MAX_DATE_LENGTH &&
                fecha[4] == '-' &&
                fecha[7] == '-' &&
                fecha.count { it.isDigit() } == 8

        if (!formatoCorrecto) return false

        val mes = fecha.substring(5, 7).toInt()
        val dia = fecha.substring(8, 10).toInt()

        return mes in 1..12 && dia in 1..31
    }

    @StringRes
    private fun mensajeDeError(error: Throwable?, @StringRes generico: Int): Int = when (error) {
        is ContenidoNoEncontradoException -> R.string.contenido_no_encontrado
        is ErrorDelServidorException -> R.string.error_servidor
        is SinConexionException -> R.string.error_sin_conexion
        else -> generico
    }
}

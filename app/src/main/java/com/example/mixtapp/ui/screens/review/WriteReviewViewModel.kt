package com.example.mixtapp.ui.screens.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.injection.IoDispatcher
import com.example.mixtapp.data.model.MAX_DATE_LENGTH
import com.example.mixtapp.data.model.MAX_REVIEW_LENGTH
import com.example.mixtapp.data.repository.CatalogoRepository
import com.example.mixtapp.data.repository.ReviewRepository
import com.example.mixtapp.ui.utils.mensajeDeError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WriteReviewViewModel @Inject constructor(
    private val catalogoRepository: CatalogoRepository,
    private val reviewRepository: ReviewRepository,
    // Las corrutinas se lanzan en este hilo; las pruebas pasan uno de prueba
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow(WriteReviewState())
    val uiState: StateFlow<WriteReviewState> = _uiState.asStateFlow()

    // Se pide una sola vez: si se volviera a pedir al mostrar la pantalla otra vez, se borraria lo
    // que ya se escribio
    private var cargado = false

    // Buscar el item es responsabilidad del ViewModel, no de la navegacion. Llega con sus resenas,
    // y entre ellas la mia si ya lo califique
    fun getItem(tipo: String, itemId: String) {
        if (cargado) return
        cargado = true

        viewModelScope.launch(ioDispatcher) {
            _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }

            val result = catalogoRepository.getItemConResenas(tipo = tipo, itemId = itemId)
            val itemConResenas = result.getOrNull()

            if (result.isSuccess && itemConResenas != null) {
                // Solo se permite una resena por item: si ya existe se edita
                val miResena = itemConResenas.reviews.find { it.isMine }

                _uiState.update {
                    it.copy(
                        item = itemConResenas.item,
                        reviewId = miResena?.id,
                        rating = miResena?.rating ?: 0,
                        reviewText = miResena?.content ?: "",
                        listenedDate = miResena?.date ?: "",
                        isLoading = false,
                        errorMessageRes = null,
                    )
                }
            } else {
                // Si fallo se puede volver a pedir
                cargado = false
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessageRes = mensajeDeError(
                            error = result.exceptionOrNull(),
                            generico = R.string.error_cargar_contenido,
                            noEncontrado = R.string.album_no_encontrado,
                        )
                    )
                }
            }
        }
    }

    // Tocar la misma estrella otra vez la quita: asi se puede dejar en 0
    fun updateRating(rating: Int) {
        val calificacionActual = _uiState.value.rating
        val nueva = if (rating == calificacionActual) 0 else rating

        _uiState.update { it.copy(rating = nueva) }
    }

    // El texto se corta en el limite: el contador nunca pasa del maximo
    fun updateReviewText(reviewText: String) {
        _uiState.update {
            it.copy(reviewText = reviewText.take(MAX_REVIEW_LENGTH), errorMessageRes = null)
        }
    }

    // La fecha no puede ser mas larga que AAAA-MM-DD
    fun updateListenedDate(listenedDate: String) {
        _uiState.update {
            it.copy(listenedDate = listenedDate.take(MAX_DATE_LENGTH), errorMessageRes = null)
        }
    }

    // Valida el formulario y guarda la resena. No se vuelve atras hasta que la base confirma
    fun publicarResena() {
        val estado = _uiState.value
        val item = estado.item ?: return
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

        viewModelScope.launch(ioDispatcher) {
            _uiState.update { it.copy(publicando = true, errorMessageRes = null) }

            val result = reviewRepository.guardarResena(
                item = item,
                rating = estado.rating,
                texto = texto,
                fecha = fecha,
                reviewId = estado.reviewId,
            )

            if (result.isSuccess) {
                _uiState.update { it.copy(publicando = false, navigate = true, errorMessageRes = null) }
            } else {
                _uiState.update {
                    it.copy(
                        publicando = false,
                        errorMessageRes = mensajeDeError(
                            error = result.exceptionOrNull(),
                            generico = R.string.error_publicar_resena,
                            noEncontrado = R.string.album_no_encontrado,
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

}

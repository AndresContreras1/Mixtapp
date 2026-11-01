package com.example.mixtapp.ui.screens.myreviews

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
class MyReviewsViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository,
    // Las corrutinas se lanzan en este hilo; las pruebas pasan uno de prueba
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyReviewsState())
    val uiState: StateFlow<MyReviewsState> = _uiState.asStateFlow()

    // Mis resenas son en vivo y no dependen de la ruta: se escuchan una sola vez al crear el
    // ViewModel. Si se pidieran en cada LaunchedEffect se acumularian listeners
    init {
        escucharMisResenas()
    }

    // Al volver de editar, borrar o recibir un like, la lista llega de nuevo sola
    private fun escucharMisResenas() {
        viewModelScope.launch(ioDispatcher) {
            reviewRepository.getMyReviewsLive()
                .catch { e ->
                    Log.d("MyReviewsViewModel", "escucharMisResenas: ${e.message}")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessageRes = mensajeDeError(error = e, generico = R.string.error_cargar_contenido),
                        )
                    }
                }
                .collect { reviews ->
                    _uiState.update {
                        it.copy(reviews = reviews, isLoading = false, errorMessageRes = null)
                    }
                }
        }
    }

    // No se quita la resena de la lista a mano: el listener trae la lista sin ella
    fun eliminarResena(reviewId: String) {
        if (_uiState.value.eliminando) return

        viewModelScope.launch(ioDispatcher) {
            _uiState.update { it.copy(eliminando = true, errorAccionRes = null) }

            val result = reviewRepository.eliminarResena(reviewId = reviewId)

            if (result.isSuccess) {
                _uiState.update { it.copy(eliminando = false, errorAccionRes = null) }
            } else {
                _uiState.update {
                    it.copy(
                        eliminando = false,
                        errorAccionRes = mensajeDeError(
                            error = result.exceptionOrNull(),
                            generico = R.string.error_eliminar_resena,
                        )
                    )
                }
            }
        }
    }

    // El corazon y el contador tampoco se cambian a mano: el listener trae la resena al dia
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
                        )
                    )
                }
            }
        }
    }
}

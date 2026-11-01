package com.example.mixtapp.ui.screens.albumdetail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.injection.IoDispatcher
import com.example.mixtapp.data.repository.AlbumRepository
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
class AlbumDetailViewModel @Inject constructor(
    private val albumRepository: AlbumRepository,
    private val reviewRepository: ReviewRepository,
    // Las corrutinas se lanzan en este hilo; las pruebas pasan uno de prueba
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlbumDetailState())
    val uiState: StateFlow<AlbumDetailState> = _uiState.asStateFlow()

    // El flujo se escucha mientras exista el ViewModel: si se volviera a pedir cada vez que la
    // pantalla se muestra, se acumularian listeners
    private var escuchandoAlbum = false

    // Datos en vivo: cada cambio en las resenas del album (una nueva, un like desde otro celular)
    // llega solo y la pantalla se vuelve a pintar, sin salir y volver a entrar
    fun escucharAlbum(albumId: String) {
        if (escuchandoAlbum) return
        escuchandoAlbum = true

        viewModelScope.launch(ioDispatcher) {
            _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }

            albumRepository.getAlbumReviewsByIdLive(albumId = albumId)
                .catch { e ->
                    Log.d("AlbumDetailViewModel", "escucharAlbum: ${e.message}")
                    // Si se cerro con error se puede volver a escuchar al entrar de nuevo
                    escuchandoAlbum = false
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessageRes = mensajeDeError(error = e, generico = R.string.error_cargar_contenido, noEncontrado = R.string.album_no_encontrado),
                        )
                    }
                }
                .collect { albumReviews ->
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
                }
        }
    }

    // Al quitar mi resena no se toca la lista a mano: el listener del detalle trae las resenas sin
    // ella y el promedio recalculado, en este celular y en los demas
    fun eliminarMiResena() {
        val miResenaId = _uiState.value.miResenaId ?: return

        if (_uiState.value.eliminando) return

        viewModelScope.launch(ioDispatcher) {
            _uiState.update { it.copy(eliminando = true, errorMessageRes = null) }

            val result = reviewRepository.eliminarResena(reviewId = miResenaId)

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(miResenaId = null, eliminando = false, errorMessageRes = null)
                }
            } else {
                _uiState.update {
                    it.copy(
                        eliminando = false,
                        errorMessageRes = mensajeDeError(
                            error = result.exceptionOrNull(),
                            generico = R.string.error_eliminar_resena, noEncontrado = R.string.album_no_encontrado,
                        )
                    )
                }
            }
        }
    }

    // Con el detalle en vivo no se copia el estado a mano: la transaccion cambia likesCount, eso
    // dispara el flujo y llega la resena con el corazon y el contador al dia, en este celular y
    // en los demas que tengan el album abierto
    fun sendOrDeleteLike(reviewId: String) {
        viewModelScope.launch(ioDispatcher) {
            val result = reviewRepository.sendOrDeleteLike(reviewId = reviewId)

            if (result.isSuccess) {
                _uiState.update { it.copy(errorMessageRes = null) }
            } else {
                _uiState.update {
                    it.copy(
                        errorMessageRes = mensajeDeError(
                            error = result.exceptionOrNull(),
                            generico = R.string.error_actualizar_like, noEncontrado = R.string.album_no_encontrado,
                        )
                    )
                }
            }
        }
    }

}

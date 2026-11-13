package com.example.mixtapp.ui.screens.mapa

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.datasource.LocationProvider
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
class MapaViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val locationProvider: LocationProvider,
    // Las corrutinas se lanzan en este hilo; las pruebas pasan uno de prueba
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapaState())
    val uiState: StateFlow<MapaState> = _uiState.asStateFlow()

    // El flujo no depende de la ruta: se escucha una sola vez al crear el ViewModel. Una resena
    // nueva aparece como marcador sin salir y volver a entrar
    init {
        escucharResenas()
    }

    private fun escucharResenas() {
        viewModelScope.launch(ioDispatcher) {
            reviewRepository.getResenasEnElMapaLive()
                .catch { e ->
                    Log.d("MapaViewModel", "escucharResenas: ${e.message}")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessageRes = mensajeDeError(error = e, generico = R.string.error_cargar_contenido),
                        )
                    }
                }
                .collect { resenas ->
                    _uiState.update { it.copy(resenas = resenas, isLoading = false, errorMessageRes = null) }
                }
        }
    }

    // La pantalla lo llama cuando ya hay permiso de ubicacion. Si no se pudo leer, queda Bogota
    fun centrarEnMiUbicacion() {
        viewModelScope.launch(ioDispatcher) {
            val ubicacion = locationProvider.getCurrentLocation() ?: return@launch

            _uiState.update { it.copy(centro = ubicacion) }
        }
    }

    // Tocar un marcador muestra la tarjeta de su resena; tocar el mismo otra vez la cierra
    fun seleccionar(reviewId: String) {
        _uiState.update {
            it.copy(seleccionadaId = if (it.seleccionadaId == reviewId) null else reviewId, errorAccionRes = null)
        }
    }

    fun cerrarSeleccion() {
        _uiState.update { it.copy(seleccionadaId = null, errorAccionRes = null) }
    }

    // El like de la tarjeta: el contador llega por el listener, no se cambia a mano
    fun sendOrDeleteLike(reviewId: String) {
        viewModelScope.launch(ioDispatcher) {
            val result = reviewRepository.sendOrDeleteLike(reviewId = reviewId)

            if (result.isSuccess) {
                _uiState.update { it.copy(errorAccionRes = null) }
            } else {
                _uiState.update {
                    it.copy(errorAccionRes = mensajeDeError(error = result.exceptionOrNull(), generico = R.string.error_me_gusta))
                }
            }
        }
    }
}

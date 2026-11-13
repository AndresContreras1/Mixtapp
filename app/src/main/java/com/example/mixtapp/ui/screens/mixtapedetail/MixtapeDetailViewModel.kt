package com.example.mixtapp.ui.screens.mixtapedetail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.injection.IoDispatcher
import com.example.mixtapp.data.repository.MixtapeRepository
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
class MixtapeDetailViewModel @Inject constructor(
    private val mixtapeRepository: MixtapeRepository,
    // Las corrutinas se lanzan en este hilo; las pruebas pasan uno de prueba
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow(MixtapeDetailState())
    val uiState: StateFlow<MixtapeDetailState> = _uiState.asStateFlow()

    // El flujo se escucha mientras exista el ViewModel: si se volviera a pedir cada vez que la
    // pantalla se muestra, se acumularian listeners
    private var escuchando = false

    // En vivo: si agrego un item desde otro detalle o lo quito aqui, la lista cambia sola
    fun getMixtape(id: String) {
        if (escuchando) return
        escuchando = true

        viewModelScope.launch(ioDispatcher) {
            mixtapeRepository.getMixtapeLive(id = id)
                .catch { e ->
                    Log.d("MixtapeDetailViewModel", "getMixtape: ${e.message}")
                    // Si se cerro con error se puede volver a escuchar al entrar de nuevo
                    escuchando = false
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessageRes = mensajeDeError(
                                error = e,
                                generico = R.string.error_cargar_contenido,
                                noEncontrado = R.string.mixtape_no_encontrada,
                            ),
                        )
                    }
                }
                .collect { mixtape ->
                    _uiState.update { it.copy(mixtape = mixtape, isLoading = false, errorMessageRes = null) }
                }
        }
    }

    // El item desaparece cuando llega la mixtape nueva por el listener: no se quita a mano
    fun quitarItem(itemId: String) {
        val mixtapeId = _uiState.value.mixtape?.id ?: return

        viewModelScope.launch(ioDispatcher) {
            val result = mixtapeRepository.quitarItem(mixtapeId = mixtapeId, itemId = itemId)

            if (result.isSuccess) {
                _uiState.update { it.copy(errorAccionRes = null) }
            } else {
                _uiState.update {
                    it.copy(errorAccionRes = mensajeDeError(error = result.exceptionOrNull(), generico = R.string.error_mixtape))
                }
            }
        }
    }

    // Si queda borrada, la pantalla vuelve atras con eliminada
    fun eliminarMixtape() {
        val mixtapeId = _uiState.value.mixtape?.id ?: return

        viewModelScope.launch(ioDispatcher) {
            val result = mixtapeRepository.eliminarMixtape(mixtapeId = mixtapeId)

            if (result.isSuccess) {
                _uiState.update { it.copy(eliminada = true, errorAccionRes = null) }
            } else {
                _uiState.update {
                    it.copy(errorAccionRes = mensajeDeError(error = result.exceptionOrNull(), generico = R.string.error_mixtape))
                }
            }
        }
    }
}

package com.example.mixtapp.ui.screens.agregaramixtape

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.injection.IoDispatcher
import com.example.mixtapp.data.repository.CatalogoRepository
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
class AgregarAMixtapeViewModel @Inject constructor(
    private val catalogoRepository: CatalogoRepository,
    private val mixtapeRepository: MixtapeRepository,
    // Las corrutinas se lanzan en este hilo; las pruebas pasan uno de prueba
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow(AgregarAMixtapeState())
    val uiState: StateFlow<AgregarAMixtapeState> = _uiState.asStateFlow()

    // Mis mixtapes no dependen de la ruta: se escuchan una sola vez al crear el ViewModel. Si creo
    // una nueva desde aqui, aparece sola al volver
    init {
        escucharMisMixtapes()
    }

    private fun escucharMisMixtapes() {
        viewModelScope.launch(ioDispatcher) {
            mixtapeRepository.getMisMixtapesLive()
                .catch { e ->
                    Log.d("AgregarAMixtapeViewModel", "escucharMisMixtapes: ${e.message}")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessageRes = mensajeDeError(error = e, generico = R.string.error_cargar_contenido),
                        )
                    }
                }
                .collect { mixtapes ->
                    _uiState.update { it.copy(mixtapes = mixtapes, isLoading = false, errorMessageRes = null) }
                }
        }
    }

    // El item llega por la ruta: la pantalla lo pide en su LaunchedEffect. Basta leerlo una vez
    fun getItem(tipo: String, itemId: String) {
        if (_uiState.value.item != null) return

        viewModelScope.launch(ioDispatcher) {
            val result = catalogoRepository.getItemConResenas(tipo = tipo, itemId = itemId)

            if (result.isSuccess) {
                _uiState.update { it.copy(item = result.getOrNull()?.item) }
            } else {
                _uiState.update {
                    it.copy(errorMessageRes = mensajeDeError(error = result.exceptionOrNull(), generico = R.string.error_cargar_contenido))
                }
            }
        }
    }

    // Agrega el item a la mixtape elegida y vuelve al detalle
    fun agregarA(mixtapeId: String) {
        val item = _uiState.value.item ?: return

        viewModelScope.launch(ioDispatcher) {
            val result = mixtapeRepository.agregarItem(mixtapeId = mixtapeId, item = item)

            if (result.isSuccess) {
                _uiState.update { it.copy(navigate = true, errorAccionRes = null) }
            } else {
                _uiState.update {
                    it.copy(errorAccionRes = mensajeDeError(error = result.exceptionOrNull(), generico = R.string.error_mixtape))
                }
            }
        }
    }
}

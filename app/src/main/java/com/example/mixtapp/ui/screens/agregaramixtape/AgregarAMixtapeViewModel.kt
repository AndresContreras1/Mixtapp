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

    // Mis mixtapes se escuchan mientras exista el ViewModel: si se volvieran a pedir cada vez que
    // la pantalla se muestra, se acumularian listeners
    private var escuchando = false

    // El item llega por la ruta: la pantalla lo pide en su LaunchedEffect. Con su id, cada mixtape
    // dice si ya lo tiene; si creo una nueva desde aqui, aparece sola al volver
    fun getItem(tipo: String, itemId: String) {
        if (escuchando) return
        escuchando = true

        escucharMisMixtapes(itemId = itemId)
        leerItem(tipo = tipo, itemId = itemId)
    }

    private fun escucharMisMixtapes(itemId: String) {
        _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }

        viewModelScope.launch(ioDispatcher) {
            mixtapeRepository.getMisMixtapesLive(itemId = itemId)
                .catch { e ->
                    Log.d("AgregarAMixtapeViewModel", "escucharMisMixtapes: ${e.message}")
                    // Si se cerro con error se puede volver a escuchar al entrar de nuevo
                    escuchando = false
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

    // Solo el item, una vez: su titulo va arriba y su copia se guarda en la mixtape. Tiene su propio
    // error, para que una lista que llega despues no lo borre
    private fun leerItem(tipo: String, itemId: String) {
        viewModelScope.launch(ioDispatcher) {
            val result = catalogoRepository.getItem(tipo = tipo, itemId = itemId)

            if (result.isSuccess) {
                _uiState.update { it.copy(item = result.getOrNull(), errorItemRes = null) }
            } else {
                _uiState.update {
                    it.copy(errorItemRes = mensajeDeError(error = result.exceptionOrNull(), generico = R.string.error_cargar_contenido))
                }
            }
        }
    }

    // Agrega el item a la mixtape elegida y vuelve al detalle. Si ya esta en esa mixtape no se
    // repite: la tarjeta lo dice con "Ya esta"
    fun agregarA(mixtapeId: String) {
        val estado = _uiState.value
        val item = estado.item ?: return
        val mixtape = estado.mixtapes.find { it.id == mixtapeId } ?: return

        if (mixtape.tieneElItem) return

        viewModelScope.launch(ioDispatcher) {
            val result = mixtapeRepository.agregarItem(mixtapeId = mixtapeId, item = item)

            if (result.isSuccess) {
                _uiState.update { it.copy(navigate = true, errorAccionRes = null) }
            } else {
                _uiState.update {
                    it.copy(
                        errorAccionRes = mensajeDeError(
                            error = result.exceptionOrNull(),
                            generico = R.string.error_mixtape,
                            noEncontrado = R.string.mixtape_no_encontrada,
                        )
                    )
                }
            }
        }
    }
}

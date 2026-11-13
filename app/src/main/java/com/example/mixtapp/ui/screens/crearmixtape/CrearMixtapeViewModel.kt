package com.example.mixtapp.ui.screens.crearmixtape

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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Los mismos limites que las reglas de Firestore
const val MAX_NOMBRE_MIXTAPE = 40
const val MAX_DESCRIPCION_MIXTAPE = 150

@HiltViewModel
class CrearMixtapeViewModel @Inject constructor(
    private val mixtapeRepository: MixtapeRepository,
    // Las corrutinas se lanzan en este hilo; las pruebas pasan uno de prueba
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow(CrearMixtapeState())
    val uiState: StateFlow<CrearMixtapeState> = _uiState.asStateFlow()

    fun updateNombre(nombre: String) {
        _uiState.update { it.copy(nombre = nombre, errorMessageRes = null) }
    }

    fun updateDescripcion(descripcion: String) {
        _uiState.update { it.copy(descripcion = descripcion, errorMessageRes = null) }
    }

    // Se valida al presionar Crear, como el registro. Si queda creada, la pantalla vuelve atras con
    // navigate y la mixtape aparece sola en el perfil por el listener
    fun crearMixtape() {
        val estado = _uiState.value
        if (estado.guardando) return

        val nombre = estado.nombre.trim()
        val descripcion = estado.descripcion.trim()
        val error = when {
            nombre.isEmpty() -> R.string.error_nombre_mixtape_vacio
            nombre.length > MAX_NOMBRE_MIXTAPE -> R.string.error_nombre_mixtape_largo
            descripcion.length > MAX_DESCRIPCION_MIXTAPE -> R.string.error_descripcion_mixtape_larga
            else -> null
        }
        if (error != null) {
            _uiState.update { it.copy(errorMessageRes = error) }
            return
        }

        _uiState.update { it.copy(guardando = true, errorMessageRes = null) }

        viewModelScope.launch(ioDispatcher) {
            val result = mixtapeRepository.crearMixtape(nombre = nombre, descripcion = descripcion)

            if (result.isSuccess) {
                _uiState.update { it.copy(guardando = false, navigate = true) }
            } else {
                _uiState.update {
                    it.copy(
                        guardando = false,
                        errorMessageRes = mensajeDeError(error = result.exceptionOrNull(), generico = R.string.error_crear_mixtape),
                    )
                }
            }
        }
    }
}

package com.example.mixtapp.ui.screens.datoscuriosos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.injection.IoDispatcher
import com.example.mixtapp.data.repository.CatalogoRepository
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
class DatosCuriososViewModel @Inject constructor(
    private val catalogoRepository: CatalogoRepository,
    // Las corrutinas se lanzan en este hilo; las pruebas pasan uno de prueba
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow(DatosCuriososState())
    val uiState: StateFlow<DatosCuriososState> = _uiState.asStateFlow()

    // Se pide una sola vez aunque la pantalla se vuelva a mostrar
    private var cargado = false

    // Los datos curiosos no cambian mientras se leen: basta con pedir el item y su historia una
    // vez, sin listener
    fun getItem(tipo: String, itemId: String) {
        if (cargado) return
        cargado = true

        viewModelScope.launch(ioDispatcher) {
            _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }

            val result = catalogoRepository.getItemConResenas(tipo = tipo, itemId = itemId)
            val itemConResenas = result.getOrNull()

            if (result.isSuccess && itemConResenas != null) {
                // El encabezado sale con el item; si la historia falla, el error va debajo
                val historiaResult = catalogoRepository.getHistoria(itemId = itemId)

                _uiState.update {
                    it.copy(
                        item = itemConResenas.item,
                        historia = historiaResult.getOrNull(),
                        isLoading = false,
                        errorMessageRes = null,
                        errorHistoriaRes = if (historiaResult.isSuccess) null else mensajeDeError(
                            error = historiaResult.exceptionOrNull(),
                            generico = R.string.error_cargar_datos_curiosos,
                            noEncontrado = R.string.sin_datos_curiosos,
                        ),
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

}

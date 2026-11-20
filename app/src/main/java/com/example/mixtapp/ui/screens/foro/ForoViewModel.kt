package com.example.mixtapp.ui.screens.foro

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.injection.IoDispatcher
import com.example.mixtapp.data.model.MAX_MENSAJE
import com.example.mixtapp.data.repository.CatalogoRepository
import com.example.mixtapp.data.repository.ComunidadRepository
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

// El foro de un album, una cancion o un artista: el item se lee una vez y los mensajes llegan en vivo
@HiltViewModel
class ForoViewModel @Inject constructor(
    private val catalogoRepository: CatalogoRepository,
    private val comunidadRepository: ComunidadRepository,
    // Las corrutinas se lanzan en este hilo; las pruebas pasan uno de prueba
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForoState())
    val uiState: StateFlow<ForoState> = _uiState.asStateFlow()

    // El listener se pide una sola vez por ViewModel, aunque la pantalla vuelva a aparecer
    private var escuchando = false

    fun abrirForo(tipo: String, itemId: String) {
        if (escuchando) return
        escuchando = true

        viewModelScope.launch(ioDispatcher) {
            val result = catalogoRepository.getItem(tipo = tipo, itemId = itemId)
            _uiState.update {
                it.copy(
                    item = result.getOrNull(),
                    isLoading = false,
                    errorMessageRes = if (result.isSuccess) {
                        null
                    } else {
                        mensajeDeError(
                            error = result.exceptionOrNull(),
                            generico = R.string.error_cargar_contenido,
                            noEncontrado = R.string.item_no_encontrado,
                        )
                    },
                )
            }
        }

        viewModelScope.launch(ioDispatcher) {
            comunidadRepository.getMensajesDelForoLive(itemId = itemId)
                .catch { e ->
                    Log.d("ForoViewModel", "abrirForo: ${e.message}")
                    _uiState.update {
                        it.copy(errorMessageRes = mensajeDeError(error = e, generico = R.string.error_cargar_contenido))
                    }
                }
                .collect { mensajes ->
                    _uiState.update { it.copy(mensajes = mensajes) }
                }
        }
    }

    fun updateTexto(texto: String) {
        _uiState.update { it.copy(texto = texto.take(MAX_MENSAJE), errorEnviarRes = null) }
    }

    // El mensaje nuevo llega por el listener: aqui solo se limpia el campo
    fun enviar() {
        val estado = _uiState.value
        val item = estado.item ?: return
        if (estado.enviando) return
        if (estado.texto.isBlank()) {
            _uiState.update { it.copy(errorEnviarRes = R.string.error_mensaje_vacio) }
            return
        }

        _uiState.update { it.copy(enviando = true, errorEnviarRes = null) }

        viewModelScope.launch(ioDispatcher) {
            val result = comunidadRepository.publicarEnForo(item = item, texto = estado.texto.trim())
            _uiState.update {
                if (result.isSuccess) {
                    it.copy(enviando = false, texto = "")
                } else {
                    it.copy(
                        enviando = false,
                        errorEnviarRes = mensajeDeError(error = result.exceptionOrNull(), generico = R.string.error_enviar_mensaje),
                    )
                }
            }
        }
    }
}

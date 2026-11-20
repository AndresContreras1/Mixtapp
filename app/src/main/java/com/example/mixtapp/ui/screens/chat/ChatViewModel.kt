package com.example.mixtapp.ui.screens.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.injection.IoDispatcher
import com.example.mixtapp.data.model.MAX_MENSAJE
import com.example.mixtapp.data.model.UsuarioDelChatUi
import com.example.mixtapp.data.repository.ComunidadRepository
import com.example.mixtapp.data.repository.UsuarioRepository
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

// El chat con otra persona: su perfil y los mensajes llegan en vivo
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val usuarioRepository: UsuarioRepository,
    private val comunidadRepository: ComunidadRepository,
    // Las corrutinas se lanzan en este hilo; las pruebas pasan uno de prueba
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatState())
    val uiState: StateFlow<ChatState> = _uiState.asStateFlow()

    // Los listeners se piden una sola vez por ViewModel
    private var escuchando = false

    fun abrirChat(otroId: String) {
        if (escuchando) return
        escuchando = true

        viewModelScope.launch(ioDispatcher) {
            usuarioRepository.getUsuarioByIdLive(id = otroId)
                .catch { e ->
                    Log.d("ChatViewModel", "abrirChat: ${e.message}")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessageRes = mensajeDeError(error = e, generico = R.string.error_cargar_contenido),
                        )
                    }
                }
                .collect { usuario ->
                    _uiState.update {
                        it.copy(
                            otro = UsuarioDelChatUi(id = usuario.id, nombre = usuario.nombre, fotoUrl = usuario.fotoUrl),
                            isLoading = false,
                        )
                    }
                }
        }

        viewModelScope.launch(ioDispatcher) {
            comunidadRepository.getMensajesDelChatLive(otroId = otroId)
                .catch { e ->
                    Log.d("ChatViewModel", "abrirChat: ${e.message}")
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
        val otro = estado.otro ?: return
        if (estado.enviando) return
        if (estado.texto.isBlank()) {
            _uiState.update { it.copy(errorEnviarRes = R.string.error_mensaje_vacio) }
            return
        }

        _uiState.update { it.copy(enviando = true, errorEnviarRes = null) }

        viewModelScope.launch(ioDispatcher) {
            val result = comunidadRepository.enviarMensaje(otro = otro, texto = estado.texto.trim())
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

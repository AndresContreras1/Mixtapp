package com.example.mixtapp.ui.screens.comunidad

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.injection.IoDispatcher
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

// Cuantos foros de lo que me gusta se sugieren
private const val FOROS_SUGERIDOS = 8

// Las pestanas de Comunidad que no son Siguiendo (esa tiene su propio ViewModel): Afinidad, Foros y
// Mensajes
@HiltViewModel
class ComunidadViewModel @Inject constructor(
    private val comunidadRepository: ComunidadRepository,
    private val usuarioRepository: UsuarioRepository,
    // Las corrutinas se lanzan en este hilo; las pruebas pasan uno de prueba
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow(ComunidadState())
    val uiState: StateFlow<ComunidadState> = _uiState.asStateFlow()

    // Los flujos no dependen de la ruta: se escuchan una sola vez al crear el ViewModel. La afinidad
    // se calcula una vez, porque compara los likes de todos
    init {
        cargarAfinidad()
        escucharForos()
        escucharFavoritos()
        escucharChats()
    }

    fun selectTab(id: String) {
        _uiState.update { it.copy(selectedTabId = id) }
    }

    fun cargarAfinidad() {
        _uiState.update { it.copy(cargandoAfinidad = true, errorAfinidadRes = null) }

        viewModelScope.launch(ioDispatcher) {
            val result = comunidadRepository.getAfinidades()
            _uiState.update {
                if (result.isSuccess) {
                    it.copy(afinidades = result.getOrNull() ?: emptyList(), cargandoAfinidad = false)
                } else {
                    it.copy(
                        cargandoAfinidad = false,
                        errorAfinidadRes = mensajeDeError(error = result.exceptionOrNull(), generico = R.string.error_afinidad),
                    )
                }
            }
        }
    }

    private fun escucharForos() {
        _uiState.update { it.copy(cargandoForos = true, errorForosRes = null) }

        viewModelScope.launch(ioDispatcher) {
            comunidadRepository.getForosLive()
                .catch { e ->
                    Log.d("ComunidadViewModel", "escucharForos: ${e.message}")
                    _uiState.update {
                        it.copy(
                            cargandoForos = false,
                            errorForosRes = mensajeDeError(error = e, generico = R.string.error_cargar_contenido),
                        )
                    }
                }
                .collect { foros ->
                    _uiState.update { armarSugeridos(it.copy(foros = foros, cargandoForos = false, errorForosRes = null)) }
                }
        }
    }

    private fun escucharFavoritos() {
        viewModelScope.launch(ioDispatcher) {
            usuarioRepository.getMisFavoritosLive()
                .catch { e -> Log.d("ComunidadViewModel", "escucharFavoritos: ${e.message}") }
                .collect { favoritos ->
                    _uiState.update { armarSugeridos(it.copy(favoritos = favoritos)) }
                }
        }
    }

    private fun escucharChats() {
        _uiState.update { it.copy(cargandoChats = true, errorChatsRes = null) }

        viewModelScope.launch(ioDispatcher) {
            comunidadRepository.getChatsLive()
                .catch { e ->
                    Log.d("ComunidadViewModel", "escucharChats: ${e.message}")
                    _uiState.update {
                        it.copy(
                            cargandoChats = false,
                            errorChatsRes = mensajeDeError(error = e, generico = R.string.error_cargar_contenido),
                        )
                    }
                }
                .collect { chats ->
                    _uiState.update { it.copy(chats = chats, cargandoChats = false, errorChatsRes = null) }
                }
        }
    }

    // Los foros sugeridos son lo que me gusta que todavia no tiene conversacion
    private fun armarSugeridos(estado: ComunidadState): ComunidadState {
        val conConversacion = estado.foros.map { it.item.id }.toSet()
        return estado.copy(
            forosSugeridos = estado.favoritos.filter { it.id !in conConversacion }.take(FOROS_SUGERIDOS),
        )
    }
}

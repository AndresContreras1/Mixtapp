package com.example.mixtapp.ui.screens.playlist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.injection.IoDispatcher
import com.example.mixtapp.data.model.EstadoDeReproduccion
import com.example.mixtapp.data.model.ItemUi
import com.example.mixtapp.data.model.armarPlaylists
import com.example.mixtapp.data.model.loQueMeGustaDelCatalogo
import com.example.mixtapp.data.repository.CatalogoRepository
import com.example.mixtapp.data.repository.ReproductorRepository
import com.example.mixtapp.data.repository.UsuarioRepository
import com.example.mixtapp.ui.utils.mensajeDeError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Una playlist hecha para mi: se vuelve a armar con la misma regla del inicio (data/model) y su id.
// Cada cancion suena 30 segundos y "Reproducir todo" las encadena
@HiltViewModel
class PlaylistViewModel @Inject constructor(
    private val catalogoRepository: CatalogoRepository,
    private val usuarioRepository: UsuarioRepository,
    private val reproductorRepository: ReproductorRepository,
    // Las corrutinas se lanzan en este hilo; las pruebas pasan uno de prueba
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlaylistState())
    val uiState: StateFlow<PlaylistState> = _uiState.asStateFlow()

    private var playlistId: String = ""
    private var escuchando = false

    // La corrutina que escucha el fragmento que suena; null si no suena nada
    private var reproduccion: Job? = null

    fun abrirPlaylist(id: String) {
        if (escuchando) return
        escuchando = true
        playlistId = id

        viewModelScope.launch(ioDispatcher) {
            val result = catalogoRepository.getCatalogo()
            _uiState.update {
                if (result.isSuccess) {
                    armar(it.copy(catalogo = result.getOrNull() ?: emptyList(), isLoading = false))
                } else {
                    it.copy(
                        isLoading = false,
                        errorMessageRes = mensajeDeError(error = result.exceptionOrNull(), generico = R.string.error_cargar_contenido),
                    )
                }
            }
        }

        // Si mis likes fallan, la playlist se arma igual con lo que haya
        viewModelScope.launch(ioDispatcher) {
            usuarioRepository.getMisFavoritosLive()
                .catch { e -> Log.d("PlaylistViewModel", "abrirPlaylist: ${e.message}") }
                .collect { favoritos ->
                    _uiState.update { armar(it.copy(favoritos = favoritos)) }
                }
        }
    }

    // Reproducir todo: empieza por la primera; si ya sonaba toda la playlist, la detiene
    fun alternarReproducirTodo() {
        if (_uiState.value.reproduciendoTodo) {
            detenerVistaPrevia()
            return
        }
        val primera = _uiState.value.playlist?.canciones?.firstOrNull() ?: return
        reproducir(item = primera, todo = true)
    }

    // El play de una cancion: suena solo esa. Tocarlo en la que suena la detiene
    fun alternarVistaPrevia(item: ItemUi) {
        val eraEsta = _uiState.value.vistaPreviaId == item.id
        detenerVistaPrevia()
        if (!eraEsta) reproducir(item = item, todo = false)
    }

    fun detenerVistaPrevia() {
        reproduccion?.cancel()
        reproduccion = null
        _uiState.update { it.copy(vistaPreviaId = null, vistaPreviaEstado = null, reproduciendoTodo = false) }
    }

    private fun reproducir(item: ItemUi, todo: Boolean) {
        reproduccion?.cancel()

        reproduccion = viewModelScope.launch(ioDispatcher) {
            _uiState.update {
                it.copy(
                    vistaPreviaId = item.id,
                    vistaPreviaEstado = EstadoDeReproduccion.Cargando,
                    errorVistaPreviaRes = null,
                    reproduciendoTodo = todo,
                )
            }

            val result = reproductorRepository.getVistaPrevia(item = item)
            val url = result.getOrNull()
            if (url == null) {
                // En la playlist completa, una cancion sin fragmento se salta
                if (todo) siguiente(despuesDe = item) else _uiState.update {
                    it.copy(
                        vistaPreviaId = null,
                        vistaPreviaEstado = null,
                        errorVistaPreviaRes = mensajeDeError(error = result.exceptionOrNull(), generico = R.string.error_vista_previa),
                    )
                }
                return@launch
            }

            reproductorRepository.escuchar(url = url)
                .catch { e ->
                    Log.d("PlaylistViewModel", "reproducir: ${e.message}")
                    _uiState.update {
                        it.copy(
                            vistaPreviaId = null,
                            vistaPreviaEstado = null,
                            reproduciendoTodo = false,
                            errorVistaPreviaRes = mensajeDeError(error = e, generico = R.string.error_vista_previa),
                        )
                    }
                }
                .collect { estado ->
                    if (estado == EstadoDeReproduccion.Terminada) {
                        if (_uiState.value.reproduciendoTodo) {
                            siguiente(despuesDe = item)
                        } else {
                            _uiState.update { it.copy(vistaPreviaId = null, vistaPreviaEstado = null) }
                        }
                    } else {
                        _uiState.update { it.copy(vistaPreviaEstado = estado) }
                    }
                }
        }
    }

    // La cancion que sigue en la playlist; al final, se detiene
    private fun siguiente(despuesDe: ItemUi) {
        val canciones = _uiState.value.playlist?.canciones ?: emptyList()
        val proxima = canciones.getOrNull(canciones.indexOfFirst { it.id == despuesDe.id } + 1)

        if (proxima == null) {
            _uiState.update { it.copy(vistaPreviaId = null, vistaPreviaEstado = null, reproduciendoTodo = false) }
        } else {
            reproducir(item = proxima, todo = true)
        }
    }

    // La playlist con este id, armada con el catalogo y mis likes de ahora
    private fun armar(estado: PlaylistState): PlaylistState {
        if (estado.catalogo.isEmpty()) return estado

        val meGustan = loQueMeGustaDelCatalogo(
            catalogo = estado.catalogo,
            idsConMiLike = estado.favoritos.map { it.id }.toSet(),
        )
        return estado.copy(playlist = armarPlaylists(catalogo = estado.catalogo, meGustan = meGustan).find { it.id == playlistId })
    }
}

package com.example.mixtapp.ui.screens.notifications

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.injection.IoDispatcher
import com.example.mixtapp.data.model.NotificationUi
import com.example.mixtapp.data.repository.CatalogoRepository
import com.example.mixtapp.data.repository.SocialRepository
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
class NotificationsViewModel @Inject constructor(
    private val socialRepository: SocialRepository,
    private val catalogoRepository: CatalogoRepository,
    // Las corrutinas se lanzan en este hilo; las pruebas pasan uno de prueba
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsState())
    val uiState: StateFlow<NotificationsState> = _uiState.asStateFlow()

    // Los tres flujos son en vivo y no dependen de la ruta: se escuchan una sola vez al crear el
    // ViewModel. Si se pidieran en cada LaunchedEffect se acumularian listeners. Cada uno va en su
    // propio launch porque collect no termina
    init {
        escucharNotificaciones()
        escucharDiscusiones()
        escucharLanzamientos()
    }

    // La app no crea notificaciones: las crean las Cloud Functions cuando alguien da like a mi resena
    // o a mi comentario, comenta, responde o empieza a seguirme. Cada una nueva, o la marca de leida,
    // llega sola por el listener
    private fun escucharNotificaciones() {
        viewModelScope.launch(ioDispatcher) {
            socialRepository.getNotificacionesLive()
                .catch { e ->
                    Log.d("NotificationsViewModel", "escucharNotificaciones: ${e.message}")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessageRes = mensajeDeError(error = e, generico = R.string.error_cargar_contenido),
                        )
                    }
                }
                .collect { todas ->
                    // Separarlas en Hoy y Antes es logica de negocio, no de la pantalla
                    _uiState.update {
                        it.copy(
                            hoy = todas.filter { notificacion -> notificacion.isToday },
                            antes = todas.filter { notificacion -> !notificacion.isToday },
                            isLoading = false,
                            errorMessageRes = null,
                        )
                    }
                }
        }
    }

    // Las discusiones son las resenas de quienes sigo: salen del mismo flujo de Siguiendo, que ya
    // las trae de la ultima publicada o editada a la mas vieja. Una nueva llega sola
    private fun escucharDiscusiones() {
        viewModelScope.launch(ioDispatcher) {
            socialRepository.getFollowingLive()
                .catch { e ->
                    Log.d("NotificationsViewModel", "escucharDiscusiones: ${e.message}")
                    _uiState.update {
                        it.copy(
                            cargandoDiscusiones = false,
                            errorDiscusionesRes = mensajeDeError(error = e, generico = R.string.error_cargar_contenido),
                        )
                    }
                }
                .collect { following ->
                    _uiState.update {
                        it.copy(
                            discusiones = following.reviews,
                            cargandoDiscusiones = false,
                            errorDiscusionesRes = null,
                        )
                    }
                }
        }
    }

    // Lo nuevo de los artistas que me gustan: el repositorio ya los filtra y los ordena. Si doy like
    // o guardo algo de otro artista, sus lanzamientos aparecen sin salir
    private fun escucharLanzamientos() {
        viewModelScope.launch(ioDispatcher) {
            catalogoRepository.getLanzamientosLive()
                .catch { e ->
                    Log.d("NotificationsViewModel", "escucharLanzamientos: ${e.message}")
                    _uiState.update {
                        it.copy(
                            cargandoLanzamientos = false,
                            errorLanzamientosRes = mensajeDeError(error = e, generico = R.string.error_cargar_contenido),
                        )
                    }
                }
                .collect { lanzamientos ->
                    _uiState.update {
                        it.copy(
                            lanzamientos = lanzamientos,
                            cargandoLanzamientos = false,
                            errorLanzamientosRes = null,
                        )
                    }
                }
        }
    }

    // Los tres flujos ya estan cargados: cambiar de pestana no vuelve a pedir nada
    fun selectTab(id: String) {
        _uiState.update { it.copy(selectedTabId = id) }
    }

    // Abrir una notificacion la marca leida. No se cambia el estado a mano: el punto se quita
    // cuando el listener trae la notificacion leida
    fun abrirNotificacion(notificacion: NotificationUi) {
        if (notificacion.isRead) return

        viewModelScope.launch(ioDispatcher) {
            val result = socialRepository.marcarNotificacionLeida(notificacionId = notificacion.id)

            if (result.isSuccess) {
                _uiState.update { it.copy(errorAccionRes = null) }
            } else {
                _uiState.update {
                    it.copy(
                        errorAccionRes = mensajeDeError(
                            error = result.exceptionOrNull(),
                            generico = R.string.error_actualizar_lista,
                        )
                    )
                }
            }
        }
    }
}

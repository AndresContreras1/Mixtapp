package com.example.mixtapp.ui.screens.notifications

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.injection.IoDispatcher
import com.example.mixtapp.data.model.NotificationUi
import com.example.mixtapp.data.repository.SocialRepository
import com.example.mixtapp.ui.screens.notifications.model.TAB_SIN_LEER
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
    // Las corrutinas se lanzan en este hilo; las pruebas pasan uno de prueba
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsState())
    val uiState: StateFlow<NotificationsState> = _uiState.asStateFlow()

    // Las notificaciones son en vivo y no dependen de la ruta: se escuchan una sola vez al crear el
    // ViewModel. Si se pidieran en cada LaunchedEffect se acumularian listeners
    init {
        escucharNotificaciones()
    }

    // La app no crea notificaciones: las crean las Cloud Functions cuando alguien da like, comenta,
    // responde o empieza a seguirme. Cada una nueva, o la marca de leida, llega sola por el listener
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
                    _uiState.update {
                        // Al llegar datos nuevos se conserva la pestana que estaba elegida
                        val visibles = aplicarFiltro(tabId = it.selectedTabId, todas = todas)

                        it.copy(
                            todas = todas,
                            hoy = visibles.filter { notificacion -> notificacion.isToday },
                            antes = visibles.filter { notificacion -> !notificacion.isToday },
                            unreadCount = todas.count { notificacion -> !notificacion.isRead },
                            isLoading = false,
                            errorMessageRes = null,
                        )
                    }
                }
        }
    }

    // Las notificaciones ya estan cargadas: cambiar de pestana no las vuelve a pedir
    fun selectTab(id: String) {
        _uiState.update {
            val visibles = aplicarFiltro(tabId = id, todas = it.todas)

            it.copy(
                selectedTabId = id,
                hoy = visibles.filter { notificacion -> notificacion.isToday },
                antes = visibles.filter { notificacion -> !notificacion.isToday },
            )
        }
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

    // Filtrar es logica de negocio, no de la pantalla
    private fun aplicarFiltro(tabId: String, todas: List<NotificationUi>): List<NotificationUi> {
        return if (tabId == TAB_SIN_LEER) {
            todas.filter { !it.isRead }
        } else {
            todas
        }
    }
}

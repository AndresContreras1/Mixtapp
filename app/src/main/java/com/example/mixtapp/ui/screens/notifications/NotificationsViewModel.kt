package com.example.mixtapp.ui.screens.notifications

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.model.NotificationUi
import com.example.mixtapp.data.repository.ContenidoNoEncontradoException
import com.example.mixtapp.data.repository.SocialRepository
import com.example.mixtapp.ui.screens.notifications.model.NotificationSectionUi
import com.example.mixtapp.ui.screens.notifications.model.TAB_SIN_LEER
import com.example.mixtapp.ui.screens.notifications.model.notificationTabs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val socialRepository: SocialRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsState())
    val uiState: StateFlow<NotificationsState> = _uiState.asStateFlow()

    // No recibe parametros, asi que los datos se cargan al crear el ViewModel
    init {
        getNotifications()
    }

    private fun getNotifications() {
        val tabId = _uiState.value.selectedTabId

        cargarNotificaciones(tabId = tabId, generico = R.string.error_cargar_contenido)
    }

    fun updateSelectedTab(tabId: String) {
        cargarNotificaciones(tabId = tabId, generico = R.string.error_actualizar_lista)
    }

    private fun cargarNotificaciones(tabId: String, @StringRes generico: Int) {
        viewModelScope.launch {
            val result = socialRepository.getNotifications()

            if (result.isSuccess) {
                val todas = result.getOrNull() ?: emptyList()

                _uiState.update {
                    it.copy(
                        tabs = notificationTabs,
                        selectedTabId = tabId,
                        sections = agruparPorSeccion(
                            notificaciones = aplicarFiltro(tabId = tabId, todas = todas)
                        ),
                        unreadCount = contarNoLeidas(todas = todas),
                        errorMessageRes = null,
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        errorMessageRes = mensajeDeError(
                            error = result.exceptionOrNull(),
                            generico = generico,
                        )
                    )
                }
            }
        }
    }

    private fun contarNoLeidas(todas: List<NotificationUi>): Int = todas.count { !it.isRead }

    // Filtrar es logica de negocio, no de la pantalla
    private fun aplicarFiltro(tabId: String, todas: List<NotificationUi>): List<NotificationUi> {
        return if (tabId == TAB_SIN_LEER) {
            todas.filter { !it.isRead }
        } else {
            todas
        }
    }

    // Agrupar tambien es logica: la pantalla solo recorre las secciones
    private fun agruparPorSeccion(
        notificaciones: List<NotificationUi>,
    ): List<NotificationSectionUi> = notificaciones
        .groupBy { it.section }
        .map { (section, delGrupo) ->
            NotificationSectionUi(section = section, notifications = delGrupo)
        }

    @StringRes
    private fun mensajeDeError(error: Throwable?, @StringRes generico: Int): Int = when (error) {
        is ContenidoNoEncontradoException -> R.string.contenido_no_encontrado
        else -> generico
    }
}

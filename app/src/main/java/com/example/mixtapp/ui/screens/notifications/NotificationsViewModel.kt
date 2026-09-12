package com.example.mixtapp.ui.screens.notifications

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.mixtapp.data.local.LocalNotificationsProvider
import com.example.mixtapp.ui.screens.notifications.model.NotificationUi
import com.example.mixtapp.ui.screens.notifications.model.TAB_SIN_LEER
import com.example.mixtapp.ui.screens.notifications.model.notificationTabs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsState())
    val uiState: StateFlow<NotificationsState> = _uiState.asStateFlow()

    init {
        getNotifications()
    }

    private fun getNotifications() {
        _uiState.update {
            it.copy(
                tabs = notificationTabs,
                notifications = aplicarFiltro(tabId = it.selectedTabId),
                unreadCount = contarNoLeidas(),
            )
        }
    }

    fun updateSelectedTab(tabId: String) {
        _uiState.update {
            it.copy(
                selectedTabId = tabId,
                notifications = aplicarFiltro(tabId = tabId),
                unreadCount = contarNoLeidas(),
            )
        }
    }

    private fun contarNoLeidas(): Int =
        LocalNotificationsProvider.notifications.count { !it.isRead }

    // Filtrar es logica de negocio, no de la pantalla
    private fun aplicarFiltro(tabId: String): List<NotificationUi> {
        val todas = LocalNotificationsProvider.notifications
        return if (tabId == TAB_SIN_LEER) {
            todas.filter { !it.isRead }
        } else {
            todas
        }
    }
}
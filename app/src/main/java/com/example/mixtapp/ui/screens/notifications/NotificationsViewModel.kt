package com.example.mixtapp.ui.screens.notifications

import androidx.lifecycle.ViewModel
import com.example.mixtapp.data.local.LocalNotificationsProvider
import com.example.mixtapp.ui.screens.notifications.model.NotificationUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class NotificationsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsState())
    val uiState: StateFlow<NotificationsState> = _uiState.asStateFlow()

    init {
        getNotifications()
    }

    private fun getNotifications() {
        val todas = LocalNotificationsProvider.notifications

        _uiState.update {
            it.copy(
                notifications = aplicarFiltro(tab = it.selectedTab),
                unreadCount = todas.count { n -> !n.isRead },
            )
        }
    }

    fun updateSelectedTab(tab: String) {
        _uiState.update {
            it.copy(
                selectedTab = tab,
                notifications = aplicarFiltro(tab = tab),
            )
        }
    }

    // Filtrar es logica de negocio, no de la pantalla
    private fun aplicarFiltro(tab: String): List<NotificationUi> {
        val todas = LocalNotificationsProvider.notifications
        return if (tab == "Unread") todas.filter { !it.isRead } else todas
    }
}
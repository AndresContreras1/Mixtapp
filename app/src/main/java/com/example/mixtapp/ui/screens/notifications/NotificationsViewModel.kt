package com.example.mixtapp.ui.screens.notifications

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.mixtapp.data.local.LocalNotificationsProvider
import com.example.mixtapp.ui.screens.notifications.model.NotificationUi
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
        val todas = LocalNotificationsProvider.notifications

        _uiState.update {
            it.copy(
                tabs = LocalNotificationsProvider.tabs,
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
        return if (tab == LocalNotificationsProvider.TAB_UNREAD) {
            todas.filter { !it.isRead }
        } else {
            todas
        }
    }
}
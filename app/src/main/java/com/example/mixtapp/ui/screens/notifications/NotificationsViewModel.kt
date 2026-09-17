package com.example.mixtapp.ui.screens.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.data.model.NotificationUi
import com.example.mixtapp.data.repository.SocialRepository
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

    init {
        getNotifications()
    }

    private fun getNotifications() {
        viewModelScope.launch {
            val result = socialRepository.getNotifications()

            if (result.isSuccess) {
                val todas = result.getOrNull() ?: emptyList()

                _uiState.update {
                    it.copy(
                        tabs = notificationTabs,
                        notifications = aplicarFiltro(tabId = it.selectedTabId, todas = todas),
                        unreadCount = contarNoLeidas(todas = todas),
                    )
                }
            }
        }
    }

    fun updateSelectedTab(tabId: String) {
        viewModelScope.launch {
            val result = socialRepository.getNotifications()

            if (result.isSuccess) {
                val todas = result.getOrNull() ?: emptyList()

                _uiState.update {
                    it.copy(
                        selectedTabId = tabId,
                        notifications = aplicarFiltro(tabId = tabId, todas = todas),
                        unreadCount = contarNoLeidas(todas = todas),
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
}

package com.example.mixtapp.ui.screens.notifications

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.NotificationUi
import com.example.mixtapp.ui.screens.notifications.model.NotificationTabUi
import com.example.mixtapp.ui.screens.notifications.model.TAB_TODAS
import com.example.mixtapp.ui.screens.notifications.model.notificationTabs

data class NotificationsState(
    // Todas mis notificaciones, para cambiar de pestana sin volver a pedirlas
    val todas: List<NotificationUi> = emptyList(),
    // Las que se ven segun la pestana, ya separadas por el ViewModel en Hoy y Antes
    val hoy: List<NotificationUi> = emptyList(),
    val antes: List<NotificationUi> = emptyList(),
    val unreadCount: Int = 0,
    // Las pestanas las provee el estado; el componente solo las pinta
    val tabs: List<NotificationTabUi> = notificationTabs,
    val selectedTabId: String = TAB_TODAS,
    // Arranca cargando porque el ViewModel empieza a escuchar al crearse
    val isLoading: Boolean = true,
    // No se pudieron cargar: si no hay ninguna, ocupa toda la pantalla
    @StringRes val errorMessageRes: Int? = null,
    // No se pudo marcar como leida: la lista sigue visible y el error sale encima
    @StringRes val errorAccionRes: Int? = null,
)

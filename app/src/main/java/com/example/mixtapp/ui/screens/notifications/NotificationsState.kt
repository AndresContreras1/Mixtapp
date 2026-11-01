package com.example.mixtapp.ui.screens.notifications

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.ItemUi
import com.example.mixtapp.data.model.NotificationUi
import com.example.mixtapp.data.model.ReviewUi
import com.example.mixtapp.ui.screens.notifications.model.NotificationTabUi
import com.example.mixtapp.ui.screens.notifications.model.TAB_MIAS
import com.example.mixtapp.ui.screens.notifications.model.notificationTabs

data class NotificationsState(
    // Mis notificaciones, ya separadas por el ViewModel en Hoy y Antes
    val hoy: List<NotificationUi> = emptyList(),
    val antes: List<NotificationUi> = emptyList(),
    // Las resenas nuevas de quienes sigo, la mas reciente primero
    val discusiones: List<ReviewUi> = emptyList(),
    // Los albumes y las canciones de los artistas que me gustan, el mas nuevo primero
    val lanzamientos: List<ItemUi> = emptyList(),
    // Las pestanas las provee el estado; el componente solo las pinta. La elegida se conserva al
    // volver de la discusion, un perfil o un detalle
    val tabs: List<NotificationTabUi> = notificationTabs,
    val selectedTabId: String = TAB_MIAS,
    // Arrancan cargando porque el ViewModel empieza a escuchar los tres flujos al crearse
    val isLoading: Boolean = true,
    val cargandoDiscusiones: Boolean = true,
    val cargandoLanzamientos: Boolean = true,
    // Cada pestana trae su propio error: si una falla, las otras se ven. Si la pestana no tiene
    // nada que mostrar, el error la ocupa toda
    @StringRes val errorMessageRes: Int? = null,
    @StringRes val errorDiscusionesRes: Int? = null,
    @StringRes val errorLanzamientosRes: Int? = null,
    // No se pudo marcar como leida: la lista sigue visible y el error sale encima
    @StringRes val errorAccionRes: Int? = null,
)

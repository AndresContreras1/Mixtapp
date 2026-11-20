package com.example.mixtapp.ui.screens.notifications

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.ItemUi
import com.example.mixtapp.data.model.NotificationUi
import com.example.mixtapp.data.model.ReviewUi
import com.example.mixtapp.data.model.SonandoUi
import com.example.mixtapp.ui.screens.notifications.model.TAB_MIAS
import com.example.mixtapp.ui.screens.notifications.model.notificationTabs
import com.example.mixtapp.ui.utils.OpcionUi

data class NotificationsState(
    // Mis notificaciones, ya separadas por el ViewModel en Hoy y Antes
    val hoy: List<NotificationUi> = emptyList(),
    val antes: List<NotificationUi> = emptyList(),
    // Las resenas nuevas de quienes sigo, la mas reciente primero
    val discusiones: List<ReviewUi> = emptyList(),
    // Los albumes y las canciones de los artistas que me gustan, el mas nuevo primero
    val lanzamientos: List<ItemUi> = emptyList(),
    // Lo que escuchan ahora quienes sigo (y yo), arriba de las pestanas como en Siguiendo
    val sonando: List<SonandoUi> = emptyList(),
    @StringRes val errorSonandoRes: Int? = null,
    @StringRes val errorAccionSonandoRes: Int? = null,
    // Las pestanas las provee el estado; el componente solo las pinta. La elegida se conserva al
    // volver de la discusion, un perfil o un detalle
    val tabs: List<OpcionUi> = notificationTabs,
    val selectedTabId: String = TAB_MIAS,
    // Cada pestana se pone en true al empezar a escuchar su flujo y en false cuando llega o falla
    val isLoading: Boolean = false,
    val cargandoDiscusiones: Boolean = false,
    val cargandoLanzamientos: Boolean = false,
    // Cada pestana trae su propio error: si una falla, las otras se ven. Si la pestana no tiene
    // nada que mostrar, el error la ocupa toda
    @StringRes val errorMessageRes: Int? = null,
    @StringRes val errorDiscusionesRes: Int? = null,
    @StringRes val errorLanzamientosRes: Int? = null,
    // No se pudo marcar como leida: la lista sigue visible y el error sale encima
    @StringRes val errorAccionRes: Int? = null,
)

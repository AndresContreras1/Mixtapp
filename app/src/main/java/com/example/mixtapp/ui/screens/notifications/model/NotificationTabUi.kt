package com.example.mixtapp.ui.screens.notifications.model

import androidx.annotation.StringRes
import com.example.mixtapp.R

data class NotificationTabUi(
    val id: String,
    @StringRes val label: Int
)

const val TAB_MIAS = "mias"
const val TAB_DISCUSIONES = "discusiones"
const val TAB_LANZAMIENTOS = "lanzamientos"

// Mis notificaciones, las resenas nuevas de quienes sigo y lo nuevo de los artistas que me gustan
val notificationTabs = listOf(
    NotificationTabUi(id = TAB_MIAS, label = R.string.notifications_tab_mias),
    NotificationTabUi(id = TAB_DISCUSIONES, label = R.string.notifications_tab_discusiones),
    NotificationTabUi(id = TAB_LANZAMIENTOS, label = R.string.notifications_tab_lanzamientos)
)

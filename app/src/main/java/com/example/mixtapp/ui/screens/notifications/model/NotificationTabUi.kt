package com.example.mixtapp.ui.screens.notifications.model

import com.example.mixtapp.R
import com.example.mixtapp.ui.utils.OpcionUi

const val TAB_MIAS = "mias"
const val TAB_DISCUSIONES = "discusiones"
const val TAB_LANZAMIENTOS = "lanzamientos"

// Mis notificaciones, las resenas nuevas de quienes sigo y lo nuevo de los artistas que me gustan
val notificationTabs = listOf(
    OpcionUi(id = TAB_MIAS, label = R.string.notifications_tab_mias),
    OpcionUi(id = TAB_DISCUSIONES, label = R.string.notifications_tab_discusiones),
    OpcionUi(id = TAB_LANZAMIENTOS, label = R.string.notifications_tab_lanzamientos)
)

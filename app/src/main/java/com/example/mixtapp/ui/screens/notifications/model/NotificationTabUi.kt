package com.example.mixtapp.ui.screens.notifications.model

import androidx.annotation.StringRes
import com.example.mixtapp.R

data class NotificationTabUi(
    val id: String,
    @StringRes val label: Int
)

const val TAB_TODAS = "todas"
const val TAB_SIN_LEER = "sinLeer"

val notificationTabs = listOf(
    NotificationTabUi(id = TAB_TODAS, label = R.string.notifications_tab_all),
    NotificationTabUi(id = TAB_SIN_LEER, label = R.string.notifications_tab_unread)
)

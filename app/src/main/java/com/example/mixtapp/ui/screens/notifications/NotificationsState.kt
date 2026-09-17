package com.example.mixtapp.ui.screens.notifications

import com.example.mixtapp.data.model.NotificationUi
import com.example.mixtapp.ui.screens.notifications.model.NotificationTabUi
import com.example.mixtapp.ui.screens.notifications.model.TAB_TODAS

data class NotificationsState(
    // Ya vienen filtradas por el ViewModel segun la pestana elegida
    val notifications: List<NotificationUi> = emptyList(),
    val tabs: List<NotificationTabUi> = emptyList(),
    val unreadCount: Int = 0,
    val selectedTabId: String = TAB_TODAS,
)
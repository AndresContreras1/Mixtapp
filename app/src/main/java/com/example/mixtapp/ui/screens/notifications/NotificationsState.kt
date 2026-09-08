package com.example.mixtapp.ui.screens.notifications

import com.example.mixtapp.data.local.LocalNotificationsProvider
import com.example.mixtapp.ui.screens.notifications.model.NotificationUi

data class NotificationsState(
    // Ya vienen filtradas por el ViewModel segun selectedTab
    val notifications: List<NotificationUi> = emptyList(),
    val tabs: List<String> = emptyList(),
    val unreadCount: Int = 0,
    val selectedTab: String = LocalNotificationsProvider.TAB_ALL,
    val unreadTab: String = LocalNotificationsProvider.TAB_UNREAD,
)
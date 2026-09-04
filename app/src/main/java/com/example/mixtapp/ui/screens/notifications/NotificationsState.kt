package com.example.mixtapp.ui.screens.notifications

import com.example.mixtapp.ui.screens.notifications.model.NotificationUi

data class NotificationsState(
    // Ya vienen filtradas por el ViewModel segun selectedTab
    val notifications: List<NotificationUi> = emptyList(),
    val unreadCount: Int = 0,
    val selectedTab: String = "All",
)
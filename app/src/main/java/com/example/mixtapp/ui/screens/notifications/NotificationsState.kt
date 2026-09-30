package com.example.mixtapp.ui.screens.notifications

import androidx.annotation.StringRes
import com.example.mixtapp.ui.screens.notifications.model.NotificationSectionUi
import com.example.mixtapp.ui.screens.notifications.model.NotificationTabUi
import com.example.mixtapp.ui.screens.notifications.model.TAB_TODAS

data class NotificationsState(
    // Ya vienen filtradas y agrupadas por el ViewModel segun la pestana elegida
    val sections: List<NotificationSectionUi> = emptyList(),
    val tabs: List<NotificationTabUi> = emptyList(),
    val unreadCount: Int = 0,
    val selectedTabId: String = TAB_TODAS,
    @StringRes val errorMessageRes: Int? = null,
)

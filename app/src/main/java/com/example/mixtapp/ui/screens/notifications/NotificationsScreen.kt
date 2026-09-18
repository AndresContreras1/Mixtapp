package com.example.mixtapp.ui.screens.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.data.local.LocalNotificationsProvider
import com.example.mixtapp.data.model.NotificationUi
import com.example.mixtapp.ui.components.ErrorMessage
import com.example.mixtapp.ui.screens.notifications.components.NotificationRow
import com.example.mixtapp.ui.screens.notifications.components.NotificationSectionLabel
import com.example.mixtapp.ui.screens.notifications.components.NotificationsHeader
import com.example.mixtapp.ui.screens.notifications.components.NotificationsTabs
import com.example.mixtapp.ui.screens.notifications.model.NotificationTabUi
import com.example.mixtapp.ui.screens.notifications.model.TAB_TODAS
import com.example.mixtapp.ui.screens.notifications.model.notificationTabs
import com.example.mixtapp.ui.theme.MixtappTheme

@Composable
fun NotificationsScreen(
    notificationsViewModel: NotificationsViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by notificationsViewModel.uiState.collectAsState()

    NotificationsScreenContent(
        notifications = state.notifications,
        tabs = state.tabs,
        unreadCount = state.unreadCount,
        selectedTabId = state.selectedTabId,
        errorMessageRes = state.errorMessageRes,
        onTabSelected = { notificationsViewModel.updateSelectedTab(tabId = it) },
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

@Composable
fun NotificationsScreenContent(
    notifications: List<NotificationUi>,
    tabs: List<NotificationTabUi>,
    unreadCount: Int,
    selectedTabId: String,
    errorMessageRes: Int?,
    onTabSelected: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Agrupa manteniendo el orden en que ya vienen (Today antes que Earlier)
    val grouped = notifications.groupBy { it.section }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                NotificationsHeader(
                    unreadCount = unreadCount,
                    onBackClick = onBackClick,
                    modifier = Modifier.padding(top = 24.dp, bottom = 16.dp),
                )
                NotificationsTabs(
                    tabs = tabs,
                    unreadCount = unreadCount,
                    selectedTabId = selectedTabId,
                    onTabSelected = onTabSelected,
                )
                ErrorMessage(
                    messageRes = errorMessageRes,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            ) {
                grouped.forEach { (section, items) ->
                    item(key = "header_$section") {
                        NotificationSectionLabel(text = section)
                    }

                    items(items, key = { it.id }) { notification ->
                        NotificationRow(
                            notification = notification,
                            modifier = Modifier.padding(bottom = 18.dp),
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=393dp,height=852dp")
@Composable
fun NotificationsScreenPreview() {
    val todas = LocalNotificationsProvider.notifications

    MixtappTheme(darkTheme = true, dynamicColor = false) {
        NotificationsScreenContent(
            notifications = todas,
            tabs = notificationTabs,
            unreadCount = todas.count { !it.isRead },
            selectedTabId = TAB_TODAS,
            errorMessageRes = null,
            onTabSelected = {},
            onBackClick = {},
        )
    }
}
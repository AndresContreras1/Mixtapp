package com.example.mixtapp.ui.screens.notifications.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.ui.screens.notifications.model.NotificationTabUi
import com.example.mixtapp.ui.screens.notifications.model.TAB_SIN_LEER
import com.example.mixtapp.ui.screens.notifications.model.TAB_TODAS
import com.example.mixtapp.ui.screens.notifications.model.notificationTabs
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppChip

// Las pestanas Todas y Sin leer como filtros ovalados; la de sin leer dice cuantas faltan por leer
@Composable
fun NotificationsTabs(
    tabs: List<NotificationTabUi>,
    unreadCount: Int,
    selectedTabId: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        tabs.forEach { tab ->
            val esSinLeer = tab.id == TAB_SIN_LEER

            AppChip(
                text = if (esSinLeer) stringResource(tab.label, unreadCount) else stringResource(tab.label),
                isSelected = tab.id == selectedTabId,
                onClick = { onTabSelected(tab.id) },
                modifier = if (esSinLeer) Modifier.testTag("tabSinLeer") else Modifier,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun NotificationsTabsPreview() {
    MixtappTheme(darkTheme = true) {
        NotificationsTabs(
            tabs = notificationTabs,
            unreadCount = LocalPreviewProvider.notifications.count { !it.isRead },
            selectedTabId = TAB_TODAS,
            onTabSelected = {},
        )
    }
}

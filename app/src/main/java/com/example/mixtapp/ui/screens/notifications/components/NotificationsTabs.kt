package com.example.mixtapp.ui.screens.notifications.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.ui.components.AppChip
import com.example.mixtapp.ui.screens.notifications.model.NotificationTabUi
import com.example.mixtapp.ui.screens.notifications.model.TAB_SIN_LEER

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
            val label = if (tab.id == TAB_SIN_LEER) {
                stringResource(tab.label, unreadCount)
            } else {
                stringResource(tab.label)
            }

            AppChip(
                text = label,
                isSelected = tab.id == selectedTabId,
                onClick = { onTabSelected(tab.id) },
                unselectedContainerColor = Color.Transparent,
                unselectedBorderColor = MaterialTheme.colorScheme.outline,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                verticalPadding = 10.dp,
                fontSize = 13.sp,
            )
        }
    }
}

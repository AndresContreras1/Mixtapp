package com.example.mixtapp.ui.screens.notifications.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.ui.components.AppChip
import com.example.mixtapp.ui.theme.FieldBorder
import com.example.mixtapp.ui.theme.PalePink

@Composable
fun NotificationsTabs(
    tabs: List<String>,
    unreadTab: String,
    unreadCount: Int,
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        tabs.forEach { tab ->
            val label = if (tab == unreadTab) {
                stringResource(R.string.notifications_tab_unread, unreadCount)
            } else {
                stringResource(R.string.notifications_tab_all)
            }

            AppChip(
                text = label,
                isSelected = tab == selectedTab,
                onClick = { onTabSelected(tab) },
                unselectedContainerColor = Color.Transparent,
                unselectedBorderColor = FieldBorder,
                unselectedTextColor = PalePink.copy(alpha = 0.8f),
                verticalPadding = 10.dp,
            )
        }
    }
}

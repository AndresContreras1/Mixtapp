package com.example.mixtapp.ui.screens.notifications.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mixtapp.ui.components.AppChip
import com.example.mixtapp.ui.theme.FieldBorder
import com.example.mixtapp.ui.theme.PalePink

@Composable
fun NotificationsTabs(
    unreadCount: Int,
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tabs = listOf("All" to "All", "Unread" to "Unread ($unreadCount)")

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        tabs.forEach { (value, label) ->
            AppChip(
                text = label,
                isSelected = value == selectedTab,
                onClick = { onTabSelected(value) },
                unselectedContainerColor = Color.Transparent,
                unselectedBorderColor = FieldBorder,
                unselectedTextColor = PalePink.copy(alpha = 0.8f),
                verticalPadding = 10.dp,
            )
        }
    }
}

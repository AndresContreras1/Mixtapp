package com.example.mixtapp.ui.screens.notifications.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.ui.screens.notifications.model.NotificationTabUi
import com.example.mixtapp.ui.screens.notifications.model.TAB_MIAS
import com.example.mixtapp.ui.screens.notifications.model.notificationTabs
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppChip

// Las pestanas Mias, Discusiones y Lanzamientos como filtros ovalados. Las tres no caben en todos
// los celulares, asi que van en una fila con scroll. La elegida llega del estado del ViewModel
@Composable
fun NotificationsTabs(
    tabs: List<NotificationTabUi>,
    selectedTabId: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(tabs) { tab ->
            AppChip(
                text = stringResource(tab.label),
                isSelected = tab.id == selectedTabId,
                onClick = { onTabSelected(tab.id) },
                modifier = Modifier.testTag("tab_${tab.id}"),
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
            selectedTabId = TAB_MIAS,
            onTabSelected = {},
        )
    }
}

package com.example.mixtapp.ui.screens.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.ui.screens.profile.model.PESTANA_PERFIL
import com.example.mixtapp.ui.screens.profile.model.ProfileTabUi
import com.example.mixtapp.ui.screens.profile.model.profileTabs
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppChip

// Las pestanas de mi perfil como filtros ovalados. Las cuatro no caben en el ancho del celular, asi
// que van en una fila con scroll. La elegida llega del estado del ViewModel
@Composable
fun ProfileTabs(
    tabs: List<ProfileTabUi>,
    selectedTabId: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
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
fun ProfileTabsPreview() {
    MixtappTheme(darkTheme = true) {
        ProfileTabs(
            tabs = profileTabs,
            selectedTabId = PESTANA_PERFIL,
            onTabSelected = {},
        )
    }
}

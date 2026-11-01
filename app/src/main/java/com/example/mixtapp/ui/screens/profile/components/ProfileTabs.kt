package com.example.mixtapp.ui.screens.profile.components

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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

// Las pestanas de mi perfil como filtros ovalados y, al final, la pequena de Cambiar foto, que abre
// la galeria sin cambiar la pestana elegida. Van en una fila con scroll por si no caben en un
// celular angosto. La elegida llega del estado del ViewModel
@Composable
fun ProfileTabs(
    tabs: List<ProfileTabUi>,
    selectedTabId: String,
    onTabSelected: (String) -> Unit,
    subiendoImagen: Boolean,
    onImagePicked: (Uri) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items(tabs) { tab ->
            AppChip(
                text = stringResource(tab.label),
                isSelected = tab.id == selectedTabId,
                onClick = { onTabSelected(tab.id) },
                modifier = Modifier.testTag("tab_${tab.id}"),
            )
        }

        item {
            PickImageButton(
                action = onImagePicked,
                cargando = subiendoImagen
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
            subiendoImagen = false,
            onImagePicked = {},
        )
    }
}

package com.example.mixtapp.ui.screens.profile.components

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.ui.screens.profile.model.PESTANA_PERFIL
import com.example.mixtapp.ui.utils.OpcionUi
import com.example.mixtapp.ui.screens.profile.model.profileTabs
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.FilaDeOpciones

// Las pestanas de mi perfil en la fila de filtros ovalados y, a la derecha, la pequena de Cambiar
// foto, que abre la galeria sin cambiar la pestana elegida. Las pestanas se deslizan si no caben en
// un celular angosto; Cambiar foto siempre queda a la vista
@Composable
fun ProfileTabs(
    tabs: List<OpcionUi>,
    selectedTabId: String,
    onTabSelected: (String) -> Unit,
    subiendoImagen: Boolean,
    onImagePicked: (Uri) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FilaDeOpciones(
            opciones = tabs,
            elegidaId = selectedTabId,
            onOpcionClick = onTabSelected,
            prefijoTestTag = "tab",
            modifier = Modifier.weight(1f),
        )

        PickImageButton(
            action = onImagePicked,
            cargando = subiendoImagen
        )
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

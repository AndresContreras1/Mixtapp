package com.example.mixtapp.ui.screens.profile.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.mixtapp.ui.screens.profile.model.PESTANA_PERFIL
import com.example.mixtapp.ui.screens.profile.model.profileTabs
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.FilaDeOpciones
import com.example.mixtapp.ui.utils.OpcionUi

// Las pestanas de mi perfil en la fila de filtros ovalados. Solo navegan entre mi contenido: cambiar
// la foto se hace tocando la foto, asi no se mezcla con la navegacion y caben todas
@Composable
fun ProfileTabs(
    tabs: List<OpcionUi>,
    selectedTabId: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    FilaDeOpciones(
        opciones = tabs,
        elegidaId = selectedTabId,
        onOpcionClick = onTabSelected,
        prefijoTestTag = "tab",
        modifier = modifier,
    )
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

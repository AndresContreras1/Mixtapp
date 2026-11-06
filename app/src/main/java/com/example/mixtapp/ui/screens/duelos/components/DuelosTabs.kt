package com.example.mixtapp.ui.screens.duelos.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.data.model.TIPO_ALBUM
import com.example.mixtapp.ui.screens.duelos.model.DueloTabUi
import com.example.mixtapp.ui.screens.duelos.model.dueloTabs
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppChip

// Los tres duelos de hoy (albumes, canciones y artistas) como filtros ovalados. El elegido llega del
// estado del ViewModel
@Composable
fun DuelosTabs(
    tabs: List<DueloTabUi>,
    tipoElegido: String,
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
                isSelected = tab.tipo == tipoElegido,
                onClick = { onTabSelected(tab.tipo) },
                modifier = Modifier.testTag("tabDuelo_${tab.tipo}"),
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun DuelosTabsPreview() {
    MixtappTheme(darkTheme = true) {
        DuelosTabs(
            tabs = dueloTabs,
            tipoElegido = TIPO_ALBUM,
            onTabSelected = {},
        )
    }
}

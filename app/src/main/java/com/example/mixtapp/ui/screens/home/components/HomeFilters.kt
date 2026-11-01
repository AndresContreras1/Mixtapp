package com.example.mixtapp.ui.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.ui.screens.home.model.HomeFilterUi
import com.example.mixtapp.ui.screens.home.model.homeFilters
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppChip

// Los filtros del inicio: el elegido cambia lo que muestran las tres filas. Cada chip lleva su
// testTag para las pruebas e2e
@Composable
fun HomeFilters(
    filters: List<HomeFilterUi>,
    selectedId: String,
    onFilterSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        filters.forEach { filter ->
            AppChip(
                text = stringResource(filter.label),
                isSelected = filter.id == selectedId,
                onClick = { onFilterSelected(filter.id) },
                modifier = Modifier.testTag("filtro_${filter.id}"),
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun HomeFiltersPreview() {
    MixtappTheme(darkTheme = true) {
        HomeFilters(
            filters = homeFilters,
            selectedId = homeFilters.first().id,
            onFilterSelected = {},
        )
    }
}

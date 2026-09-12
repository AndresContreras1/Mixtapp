package com.example.mixtapp.ui.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.mixtapp.ui.components.AppChip
import com.example.mixtapp.ui.screens.home.model.HomeFilterUi

@Composable
fun FilterChips(
    filters: List<HomeFilterUi>,
    selectedId: String,
    onFilterSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        filters.forEach { filter ->
            AppChip(
                text = stringResource(filter.label),
                isSelected = filter.id == selectedId,
                onClick = { onFilterSelected(filter.id) },
                unselectedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unselectedBorderColor = null,
                unselectedTextColor = MaterialTheme.colorScheme.onSurface,
                verticalPadding = 12.dp
            )
        }
    }
}

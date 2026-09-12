package com.example.mixtapp.ui.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mixtapp.ui.components.AppChip

@Composable
fun FilterChips(
    filters: List<String>,
    selected: String,
    onFilterSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        filters.forEach { filter ->
            AppChip(
                text = filter,
                isSelected = filter == selected,
                onClick = { onFilterSelected(filter) },
                unselectedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unselectedBorderColor = null,
                unselectedTextColor = MaterialTheme.colorScheme.onSurface,
                verticalPadding = 12.dp
            )
        }
    }
}

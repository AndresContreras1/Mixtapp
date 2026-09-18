package com.example.mixtapp.ui.screens.following.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.ui.components.AppChip
import com.example.mixtapp.ui.screens.following.model.FollowingFilterUi

@Composable
fun FollowingFilters(
    filters: List<FollowingFilterUi>,
    selectedFilterId: String,
    onFilterSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(filters, key = { it.id }) { filter ->
            AppChip(
                text = stringResource(filter.label),
                isSelected = filter.id == selectedFilterId,
                onClick = { onFilterSelected(filter.id) },
                unselectedContainerColor = Color.Transparent,
                unselectedBorderColor = MaterialTheme.colorScheme.outline,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.62f),
                verticalPadding = 8.dp,
                fontSize = 14.sp,
            )
        }
    }
}

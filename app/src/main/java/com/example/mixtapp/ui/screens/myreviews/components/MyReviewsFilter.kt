package com.example.mixtapp.ui.screens.myreviews.components

import androidx.compose.foundation.layout.Arrangement
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
import com.example.mixtapp.ui.screens.myreviews.model.MyReviewFilterUi

@Composable
fun MyReviewsFilter(
    // Los filtros llegan del ViewModel, no se declaran aqui
    filters: List<MyReviewFilterUi>,
    selectedId: String,
    onFilterSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(filters, key = { it.id }) { filter ->
            AppChip(
                text = stringResource(filter.label),
                isSelected = filter.id == selectedId,
                onClick = { onFilterSelected(filter.id) },
                unselectedContainerColor = Color.Transparent,
                unselectedBorderColor = MaterialTheme.colorScheme.outline,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                verticalPadding = 10.dp,
                fontSize = 13.sp
            )
        }
    }
}

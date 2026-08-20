package com.example.mixtapp.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R

@Composable
fun FilterChips(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        HomeChip(text = stringResource(R.string.for_you), isSelected = true)
        HomeChip(text = stringResource(R.string.trending), isSelected = false)
        HomeChip(text = stringResource(R.string.friends), isSelected = false)
    }
}
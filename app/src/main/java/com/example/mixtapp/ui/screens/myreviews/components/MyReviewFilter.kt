package com.example.mixtapp.ui.screens.myreviews.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.ui.theme.FieldBorder
import com.example.mixtapp.ui.theme.PalePink
import com.example.mixtapp.ui.theme.PrimaryPink

private val filters = listOf("Recent", "Top Rated", "A-Z", "5", "4")

@Composable
fun MyReviewsFilter(
    selected: String,
    onFilterSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(filters) { filter ->
            val isSelected = filter == selected
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .then(
                        if (isSelected) Modifier.background(PrimaryPink)
                        else Modifier.border(BorderStroke(1.dp, FieldBorder), RoundedCornerShape(20.dp))
                    )
                    .clickable { onFilterSelected(filter) }
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = filter,
                    color = if (isSelected) Color.White else PalePink.copy(alpha = 0.8f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
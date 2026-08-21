package com.example.mixtapp.ui.screens.review.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.ui.theme.FieldBorder
import com.example.mixtapp.ui.theme.PalePink
import com.example.mixtapp.ui.theme.PrimaryPink

@Composable
fun MoodVibeSection(
    moods: List<String>,
    selectedMoods: List<String>,
    onMoodClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "M o o d  &  v i b e",
            color = PalePink,
            fontSize = 18.sp
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            moods.chunked(4).forEach { rowMoods ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowMoods.forEach { mood ->
                        MoodChip(
                            mood = mood,
                            selected = mood in selectedMoods,
                            onClick = { onMoodClick(mood) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MoodChip(
    mood: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = mood,
        color = PalePink.copy(alpha = if (selected) 0.96f else 0.62f),
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .heightIn(min = 30.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(if (selected) PrimaryPink.copy(alpha = 0.34f) else Color.Transparent)
            .border(
                BorderStroke(1.dp, if (selected) PrimaryPink else FieldBorder),
                RoundedCornerShape(18.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 13.dp, vertical = 7.dp)
    )
}

package com.example.mixtapp.ui.screens.review.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mixtapp.ui.theme.FieldBorder
import com.example.mixtapp.ui.theme.PrimaryPink

@Composable
fun ReviewActionsCard(
    listenedDate: String,
    isFavorite: Boolean,
    hasPosted: Boolean,
    onDateChange: (String) -> Unit,
    onDatePickerClick: () -> Unit,
    onFavoriteChange: (Boolean) -> Unit,
    onPostClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF171314))
                .border(BorderStroke(1.dp, FieldBorder), RoundedCornerShape(10.dp))
                .padding(horizontal = 18.dp, vertical = 14.dp)
        ) {
            DateListenedRow(
                listenedDate = listenedDate,
                onDateChange = onDateChange,
                onDatePickerClick = onDatePickerClick
            )

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = PrimaryPink.copy(alpha = 0.42f))
            Spacer(modifier = Modifier.height(12.dp))

            FavoriteRow(
                isFavorite = isFavorite,
                onFavoriteChange = onFavoriteChange
            )
        }

        Spacer(modifier = Modifier.height(26.dp))

        PostButton(
            hasPosted = hasPosted,
            onClick = onPostClick
        )
    }
}

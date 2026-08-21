package com.example.mixtapp.ui.screens.review.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.ui.theme.FieldBorder
import com.example.mixtapp.ui.theme.PalePink
import com.example.mixtapp.ui.theme.PrimaryPink

@Composable
fun ReviewActionsCard(
    listenedDate: String,
    isFavorite: Boolean,
    hasPosted: Boolean,
    onDateChange: (String) -> Unit,
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
                onDateChange = onDateChange
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

@Composable
private fun DateListenedRow(
    listenedDate: String,
    onDateChange: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Date Listened",
                color = PalePink,
                fontSize = 15.sp
            )
            Text(
                text = "When did you listen?",
                color = PalePink.copy(alpha = 0.58f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Row(
            modifier = Modifier
                .width(146.dp)
                .height(31.dp)
                .clip(RoundedCornerShape(9.dp))
                .border(BorderStroke(1.dp, FieldBorder), RoundedCornerShape(9.dp))
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = listenedDate,
                onValueChange = { onDateChange(it.take(10)) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                textStyle = TextStyle(
                    color = PalePink.copy(alpha = 0.72f),
                    fontSize = 14.sp
                )
            )

            Spacer(modifier = Modifier.width(6.dp))

            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = null,
                tint = PrimaryPink,
                modifier = Modifier.clickable { onDateChange("20/08/2026") }
            )
        }
    }
}

@Composable
private fun FavoriteRow(
    isFavorite: Boolean,
    onFavoriteChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Favorite",
                color = PalePink,
                fontSize = 15.sp
            )
            Text(
                text = "Mark as favorite?",
                color = PalePink.copy(alpha = 0.58f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            tint = if (isFavorite) PrimaryPink else PrimaryPink.copy(alpha = 0.78f),
            modifier = Modifier
                .size(36.dp)
                .clickable { onFavoriteChange(!isFavorite) }
        )
    }
}

@Composable
private fun PostButton(
    hasPosted: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(if (hasPosted) PrimaryPink.copy(alpha = 0.62f) else PrimaryPink.copy(alpha = 0.34f))
            .border(BorderStroke(1.dp, FieldBorder.copy(alpha = 0.7f)), RoundedCornerShape(4.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (hasPosted) "POSTED" else "POST",
            color = PrimaryPink,
            fontSize = 14.sp,
            fontWeight = FontWeight.Black
        )
    }
}

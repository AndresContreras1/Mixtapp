package com.example.mixtapp.ui.screens.profile.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.data.model.RecentActivityUi
import com.example.mixtapp.ui.components.AlbumAsyncImage
import com.example.mixtapp.ui.components.StarRating

@Composable
fun RecentActivitySection(
    activity: RecentActivityUi,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.actividad_reciente).uppercase(),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            AlbumAsyncImage(
                cover = activity.album.cover,
                contentDescription = stringResource(R.string.album_cover, activity.album.title),
                alpha = 1f,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = activity.album.title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = stringResource(
                        R.string.reviewed_today_rating,
                        stringResource(R.string.reviewed_today),
                        activity.rating,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                StarRating(
                    rating = activity.rating,
                    starCount = activity.rating,
                    starSize = 16.dp,
                    spacing = 4.dp,
                    filledTint = MaterialTheme.colorScheme.primary,
                    emptyTint = MaterialTheme.colorScheme.primary,
                    emptyIcon = Icons.Filled.Star,
                    onRatingChange = null
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = activity.comment,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(56.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
    }
}
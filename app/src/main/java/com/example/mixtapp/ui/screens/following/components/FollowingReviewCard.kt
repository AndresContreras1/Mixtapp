package com.example.mixtapp.ui.screens.following.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.data.model.FollowingReviewUi
import com.example.mixtapp.ui.components.ReviewActionsRow
import com.example.mixtapp.ui.components.ReviewAlbumRow
import com.example.mixtapp.ui.components.ReviewAuthorRow

@Composable
fun FollowingReviewCard(
    review: FollowingReviewUi,
    isLiked: Boolean,
    isShared: Boolean,
    onLikeClick: () -> Unit,
    onShareClick: () -> Unit,
    onCommentsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 18.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.58f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.8f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            ReviewAuthorRow(
                reviewerName = review.reviewerName,
                avatarText = review.reviewerInitials,
                reviewedAt = review.reviewedAt,
                avatarSize = 44.dp,
                avatarBrush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.tertiaryContainer,
                        MaterialTheme.colorScheme.secondary,
                    )
                ),
                avatarTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                nameFontSize = 15.sp,
                dateFontSize = 12.sp,
            )

            ReviewAlbumRow(
                cover = review.album.cover,
                albumTitle = review.album.title,
                artistName = review.album.artist,
                rating = review.rating,
                coverSize = 88.dp,
                coverCorner = 12.dp,
                starSize = 16.dp,
                modifier = Modifier.padding(top = 18.dp),
            )

            Text(
                text = review.reviewText,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(10.dp),
                    )
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.68f),
                fontSize = 14.sp,
                lineHeight = 19.sp,
                fontWeight = FontWeight.Bold,
            )

            ReviewActionsRow(
                likes = review.likes,
                isLiked = isLiked,
                onLikeClick = onLikeClick,
                commentsLabel = review.comments.toString(),
                onCommentsClick = onCommentsClick,
                isShared = isShared,
                onShareClick = onShareClick,
                modifier = Modifier.padding(top = 10.dp),
            )
        }
    }
}

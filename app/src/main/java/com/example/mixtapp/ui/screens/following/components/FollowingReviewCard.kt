package com.example.mixtapp.ui.screens.following.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.ui.components.ReviewActionsRow
import com.example.mixtapp.ui.components.ReviewAlbumRow
import com.example.mixtapp.ui.components.ReviewAuthorRow
import com.example.mixtapp.ui.screens.following.model.FollowingReviewUi
import com.example.mixtapp.ui.theme.CircleBerry
import com.example.mixtapp.ui.theme.CircleWine
import com.example.mixtapp.ui.theme.FieldBorder
import com.example.mixtapp.ui.theme.PalePink

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
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 18.dp)
            .border(1.dp, FieldBorder.copy(alpha = 0.8f), RoundedCornerShape(12.dp)),
        color = Color.Black.copy(alpha = 0.58f),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            ReviewAuthorRow(
                reviewerName = review.reviewerName,
                avatarText = review.reviewerInitials,
                reviewedAt = review.reviewedAt,
                avatarSize = 44.dp,
                avatarBrush = Brush.linearGradient(
                    colors = listOf(CircleBerry, Color(0xFFF5C25E), CircleWine)
                ),
                avatarTextColor = PalePink,
                nameFontSize = 15.sp,
                dateFontSize = 12.sp,
            )

            ReviewAlbumRow(
                cover = review.cover,
                albumTitle = review.albumTitle,
                artistName = review.artistName,
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
                        color = Color.White.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(10.dp),
                    )
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                color = Color.White.copy(alpha = 0.68f),
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

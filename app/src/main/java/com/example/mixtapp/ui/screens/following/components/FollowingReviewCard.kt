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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.ui.screens.following.model.FollowingReviewUi
import com.example.mixtapp.ui.theme.FieldBorder

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
            FollowingReviewAuthorRow(review = review)

            FollowingAlbumRow(review = review)

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

            FollowingReviewActions(
                likes = review.likes,
                comments = review.comments,
                isLiked = isLiked,
                isShared = isShared,
                onLikeClick = onLikeClick,
                onShareClick = onShareClick,
                onCommentsClick = onCommentsClick,
            )
        }
    }
}

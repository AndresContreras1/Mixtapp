package com.example.mixtapp.ui.screens.discussion.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.example.mixtapp.ui.screens.discussion.model.DiscussionReviewUi
import com.example.mixtapp.ui.theme.FieldBorder

@Composable
fun DiscussionReviewCard(
    review: DiscussionReviewUi,
    isLiked: Boolean,
    isShared: Boolean,
    onLikeClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, FieldBorder.copy(alpha = 0.84f), RoundedCornerShape(12.dp)),
        color = Color.Black.copy(alpha = 0.56f),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            ReviewAuthorRow(review = review)

            Spacer(modifier = Modifier.height(14.dp))

            DiscussionAlbumRow(review = review)

            Text(
                text = review.reviewText,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(10.dp),
                    )
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                color = Color.White.copy(alpha = 0.66f),
                fontSize = 14.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Bold,
            )

            DiscussionReviewActions(
                likes = review.likes,
                commentsCount = review.commentsCount,
                isLiked = isLiked,
                isShared = isShared,
                onLikeClick = onLikeClick,
                onShareClick = onShareClick,
            )
        }
    }
}

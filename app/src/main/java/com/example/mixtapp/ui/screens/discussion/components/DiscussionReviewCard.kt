package com.example.mixtapp.ui.screens.discussion.components

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.ui.components.StarRating
import com.example.mixtapp.ui.screens.discussion.model.DiscussionReviewUi
import com.example.mixtapp.ui.theme.CircleWine
import com.example.mixtapp.ui.theme.FieldBorder
import com.example.mixtapp.ui.theme.PalePink
import com.example.mixtapp.ui.theme.PrimaryPink
import com.example.mixtapp.ui.theme.TextPink

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

            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = review.coverRes),
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop,
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = review.albumTitle,
                        color = Color.White,
                        fontSize = 22.sp,
                        lineHeight = 25.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Black,
                    )
                    Text(
                        text = review.artistName,
                        color = TextPink.copy(alpha = 0.74f),
                        fontSize = 14.sp,
                        lineHeight = 18.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                    )
                    StarRating(
                        rating = review.rating,
                        starCount = 5,
                        starSize = 14.dp,
                        spacing = 0.dp,
                        filledTint = PrimaryPink,
                        emptyTint = TextPink.copy(alpha = 0.32f),
                        emptyIcon = Icons.Filled.Star,
                        onRatingChange = null,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }

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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Like review",
                    tint = if (isLiked) PrimaryPink else PalePink.copy(alpha = 0.55f),
                    modifier = Modifier
                        .size(16.dp)
                        .clickable { onLikeClick() },
                )
                Text(
                    text = (review.likes + if (isLiked) 1 else 0).toString(),
                    color = PalePink.copy(alpha = 0.55f),
                    fontSize = 12.sp,
                )

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.Outlined.ChatBubbleOutline,
                    contentDescription = null,
                    tint = PalePink.copy(alpha = 0.55f),
                    modifier = Modifier.size(15.dp),
                )
                Text(
                    text = "${review.commentsCount} comments",
                    color = PalePink.copy(alpha = 0.55f),
                    fontSize = 12.sp,
                )

                Spacer(modifier = Modifier.weight(1f))

                Icon(
                    imageVector = Icons.Outlined.Share,
                    contentDescription = "Share review",
                    tint = if (isShared) PrimaryPink else PalePink.copy(alpha = 0.6f),
                    modifier = Modifier
                        .size(18.dp)
                        .clickable { onShareClick() },
                )
            }
        }
    }
}

@Composable
private fun ReviewAuthorRow(review: DiscussionReviewUi) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(CircleWine),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = review.reviewerAvatarText,
                color = PrimaryPink,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = review.reviewerName,
            color = PalePink,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = " Reviewed",
            color = TextPink.copy(alpha = 0.56f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = review.reviewedAt,
            color = TextPink.copy(alpha = 0.58f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

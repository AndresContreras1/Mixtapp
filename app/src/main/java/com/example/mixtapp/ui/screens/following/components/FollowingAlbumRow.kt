package com.example.mixtapp.ui.screens.following.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
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
import com.example.mixtapp.ui.screens.following.model.FollowingReviewUi
import com.example.mixtapp.ui.theme.PrimaryPink
import com.example.mixtapp.ui.theme.TextPink

@Composable
fun FollowingAlbumRow(
    review: FollowingReviewUi,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(top = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = review.coverRes),
            contentDescription = null,
            modifier = Modifier
                .size(88.dp)
                .clip(RoundedCornerShape(12.dp)),
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
                starSize = 16.dp,
                spacing = 0.dp,
                filledTint = PrimaryPink,
                emptyTint = TextPink.copy(alpha = 0.32f),
                emptyIcon = Icons.Filled.Star,
                onRatingChange = null,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

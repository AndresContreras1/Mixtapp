package com.example.mixtapp.ui.screens.songreview.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.ui.components.StarRating
import com.example.mixtapp.ui.screens.songreview.model.SongReviewItemUi
import com.example.mixtapp.ui.theme.PrimaryPink
import com.example.mixtapp.ui.theme.SurfaceCard

@Composable
fun ReviewsSection(
    reviews: List<SongReviewItemUi>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Reviews",
                color = Color.White,
                fontSize = 24.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Write One",
                color = PrimaryPink,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        reviews.forEach { review ->
            ReviewItem(
                author = review.author,
                daysAgo = review.daysAgo,
                rating = review.rating,
                content = review.content,
                likes = review.likes
            )
        }
    }
}

@Composable
private fun ReviewItem(
    author: String,
    daysAgo: String,
    rating: Int,
    content: String,
    likes: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.Transparent,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SurfaceCard),
                    contentAlignment = Alignment.Center
                ) {
                    Text(author.take(2).lowercase(), color = PrimaryPink, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(author, color = Color.White, fontWeight = FontWeight.Bold)
                    StarRating(
                        rating = rating,
                        // Solo se dibujan las estrellas obtenidas, sin ranuras vacias
                        starCount = rating,
                        starSize = 12.dp,
                        spacing = 0.dp,
                        filledTint = PrimaryPink,
                        emptyTint = PrimaryPink,
                        emptyIcon = Icons.Filled.Star,
                        onRatingChange = null
                    )
                }
                Text(daysAgo, color = Color.White.copy(alpha = 0.4f), fontSize = 10.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = content,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 12.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.FavoriteBorder, null, tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(likes.toString(), color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(R.string.reply),
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

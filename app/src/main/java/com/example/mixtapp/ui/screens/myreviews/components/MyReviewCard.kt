package com.example.mixtapp.ui.screens.myreviews.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
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
import com.example.mixtapp.ui.screens.myreviews.model.MyReviewUi
import com.example.mixtapp.ui.theme.FieldBorder
import com.example.mixtapp.ui.theme.PalePink
import com.example.mixtapp.ui.theme.PrimaryPink
import com.example.mixtapp.ui.theme.TextPink

@Composable
fun MyReviewCard(
    review: MyReviewUi,
    onReviewClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF171314))
            .border(BorderStroke(1.dp, FieldBorder.copy(alpha = 0.5f)), RoundedCornerShape(14.dp))
            .clickable { onReviewClick(review.songId) }
            .padding(16.dp)
    ) {
        ReviewScoreBadge(
            score = review.score,
            modifier = Modifier.align(Alignment.TopEnd)
        )

        Column(modifier = Modifier.padding(end = 44.dp)) {
            ReviewCardHeader(review = review)

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = review.excerpt,
                color = PalePink.copy(alpha = 0.85f),
                fontSize = 13.sp,
                lineHeight = 18.sp,
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(12.dp))

            ReviewCardFooter(review = review)
        }
    }
}

// Badge de score, arriba a la derecha
@Composable
private fun ReviewScoreBadge(
    score: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        color = PrimaryPink,
        shape = RoundedCornerShape(10.dp),
        modifier = modifier.size(36.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = score.toString(),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Portada + titulo/artista/estrellas
@Composable
private fun ReviewCardHeader(
    review: MyReviewUi,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top
    ) {
        Image(
            painter = painterResource(id = review.coverRes),
            contentDescription = review.title,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(10.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Text(
                text = review.title,
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Serif,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = review.artist,
                color = PalePink.copy(alpha = 0.72f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(6.dp))
            StarRating(
                rating = review.rating,
                starCount = 5,
                starSize = 15.dp,
                spacing = 0.dp,
                filledTint = TextPink,
                emptyTint = TextPink,
                emptyIcon = Icons.Default.StarBorder,
                onRatingChange = null
            )
        }
    }
}

// Tags + likes/fecha
@Composable
private fun ReviewCardFooter(
    review: MyReviewUi,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ReviewTags(tags = review.tags)

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = null,
                tint = PalePink.copy(alpha = 0.7f),
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${review.duration}   ${review.date}",
                color = PalePink.copy(alpha = 0.6f),
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun ReviewTags(
    tags: List<String>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tags.forEach { tag ->
            Surface(
                color = PrimaryPink.copy(alpha = 0.35f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = tag,
                    color = TextPink,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

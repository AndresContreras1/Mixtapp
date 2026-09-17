package com.example.mixtapp.ui.screens.myreviews.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.data.model.MyReviewUi
import com.example.mixtapp.ui.components.AlbumAsyncImage
import com.example.mixtapp.ui.components.StarRating

// Portada + titulo/artista/estrellas
@Composable
fun ReviewCardHeader(
    review: MyReviewUi,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top
    ) {
        AlbumAsyncImage(
            cover = review.cover,
            contentDescription = stringResource(R.string.album_cover, review.title),
            alpha = 1f,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(10.dp))
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Text(
                text = review.title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Serif,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = review.artist,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.72f),
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
                filledTint = MaterialTheme.colorScheme.onBackground,
                emptyTint = MaterialTheme.colorScheme.onBackground,
                emptyIcon = Icons.Default.StarBorder,
                onRatingChange = null
            )
        }
    }
}

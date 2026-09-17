package com.example.mixtapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.AlbumCovers
import com.example.mixtapp.ui.theme.MixtappTheme

@Composable
fun ReviewAlbumRow(
    cover: String,
    albumTitle: String,
    artistName: String,
    rating: Int,
    coverSize: Dp,
    coverCorner: Dp,
    starSize: Dp,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AlbumAsyncImage(
            cover = cover,
            contentDescription = stringResource(R.string.album_cover, albumTitle),
            alpha = 1f,
            modifier = Modifier
                .size(coverSize)
                .clip(RoundedCornerShape(coverCorner)),
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Text(
                text = albumTitle,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 22.sp,
                lineHeight = 25.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Black,
            )
            Text(
                text = artistName,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.74f),
                fontSize = 14.sp,
                lineHeight = 18.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
            )
            StarRating(
                rating = rating,
                starCount = 5,
                starSize = starSize,
                spacing = 0.dp,
                filledTint = MaterialTheme.colorScheme.primary,
                emptyTint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.32f),
                emptyIcon = Icons.Filled.Star,
                onRatingChange = null,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}


@Composable
@Preview
fun ReviewAlbumRowPreview() {
    MixtappTheme(darkTheme = true, dynamicColor = false) {
        ReviewAlbumRow(
            cover = AlbumCovers.FROM_ZERO,
            albumTitle = "From Zero",
            artistName = "Linkin Park",
            rating = 4,
            coverSize = 80.dp,
            coverCorner = 10.dp,
            starSize = 14.dp
        )
    }
}

package com.example.mixtapp.ui.screens.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalAlbumReviewsProvider
import com.example.mixtapp.data.model.AlbumReviewsUi
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AlbumAsyncImage
import com.example.mixtapp.ui.utils.StarRating

@Composable
fun TrendingCard(
    albumReviews: AlbumReviewsUi,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = { onClick(albumReviews.album.id) },
        modifier = modifier
            .fillMaxWidth()
            .height(190.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AlbumAsyncImage(
                cover = albumReviews.album.cover,
                contentDescription = stringResource(R.string.album_cover, albumReviews.album.title),
                alpha = 0.4f,
                modifier = Modifier.fillMaxSize()
            )

            Box(modifier = Modifier.fillMaxSize().padding(14.dp)) {
                Text(
                    text = stringResource(R.string.now_trending),
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Column(
                    modifier = Modifier.align(Alignment.BottomStart)
                ) {
                    Text(
                        text = stringResource(R.string.titulo_guion_subtitulo, albumReviews.album.title, albumReviews.album.artist),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        StarRating(
                            rating = albumReviews.estrellas,
                            starCount = 5,
                            starSize = 14.dp,
                            spacing = 0.dp,
                            filledTint = MaterialTheme.colorScheme.onBackground,
                            emptyTint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                            emptyIcon = Icons.Default.Star,
                            onRatingChange = null
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = if (albumReviews.ratingCount == "1") {
                                stringResource(R.string.una_resena)
                            } else {
                                stringResource(R.string.n_resenas, albumReviews.ratingCount)
                            },
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun TrendingCardPreview() {
    MixtappTheme(darkTheme = true) {
        TrendingCard(
            albumReviews = LocalAlbumReviewsProvider.trendingAlbum,
            onClick = {},
        )
    }
}

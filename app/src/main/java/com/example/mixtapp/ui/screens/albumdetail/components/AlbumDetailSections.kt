package com.example.mixtapp.ui.screens.albumdetail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalAlbumReviewsProvider
import com.example.mixtapp.data.model.AlbumReviewsUi
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.theme.displayFontFamily
import com.example.mixtapp.ui.utils.BackButton
import com.example.mixtapp.ui.utils.ErrorMessage

@Composable
fun AlbumDetailSections(
    albumReviews: AlbumReviewsUi,
    miResenaId: String?,
    onLikeClick: (String) -> Unit,
    errorMessageRes: Int?,
    onWriteReviewClick: () -> Unit,
    onDeleteMyReviewClick: () -> Unit,
    onBackClick: () -> Unit,
    onAuthorClick: (String) -> Unit,
    onCommentsClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 28.dp)
    ) {
        item {
            Column {
                BackButton(onBackClick = onBackClick)

                Spacer(modifier = Modifier.height(16.dp))

                ReleaseImage(cover = albumReviews.album.cover, albumTitle = albumReviews.album.title)

                Spacer(modifier = Modifier.height(24.dp))

                ReleaseTags(tags = albumReviews.album.tags)

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = albumReviews.album.title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 28.sp,
                    fontFamily = displayFontFamily,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = albumReviews.album.artist,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                ReleaseStats(
                    rating = albumReviews.rating,
                    estrellas = albumReviews.estrellas,
                    ratingCount = albumReviews.ratingCount,
                    recommendRate = albumReviews.recommendRate
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Tocar la tarjeta abre la pantalla para escribir o editar mi resena
                UserRatingSection(
                    rating = albumReviews.userRating,
                    onClick = onWriteReviewClick
                )

                ErrorMessage(
                    messageRes = errorMessageRes,
                    modifier = Modifier.padding(top = 12.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        item {
            ReviewsHeader(
                tieneResena = miResenaId != null,
                onWriteReviewClick = onWriteReviewClick,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        if (albumReviews.reviews.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.sin_resenas_album),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
            }
        }

        // Los comentarios del album son sus resenas guardadas
        items(albumReviews.reviews.size) { index ->
            val review = albumReviews.reviews[index]

            ReviewItem(
                review = review,
                onLikeClick = { onLikeClick(review.id) },
                onEditClick = onWriteReviewClick,
                onDeleteClick = onDeleteMyReviewClick,
                onAuthorClick = { onAuthorClick(review.authorId) },
                onCommentsClick = { onCommentsClick(review.id) },
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
    }
}

@Composable
@Preview(showBackground = true, device = "spec:width=393dp,height=852dp")
fun AlbumDetailSectionsPreview() {
    MixtappTheme(darkTheme = true) {
        AlbumDetailSections(
            albumReviews = LocalAlbumReviewsProvider.albumReviews.first(),
            miResenaId = null,
            onLikeClick = {},
            errorMessageRes = null,
            onWriteReviewClick = {},
            onDeleteMyReviewClick = {},
            onBackClick = {},
            onAuthorClick = {},
            onCommentsClick = {},
        )
    }
}

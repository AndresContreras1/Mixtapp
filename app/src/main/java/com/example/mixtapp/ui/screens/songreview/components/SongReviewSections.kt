package com.example.mixtapp.ui.screens.songreview.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.data.model.SongReviewUi
import com.example.mixtapp.ui.components.ErrorMessage
import com.example.mixtapp.ui.theme.displayFontFamily

@Composable
fun SongReviewSections(
    songReview: SongReviewUi,
    userRating: Int,
    isSaved: Boolean,
    isLiked: Boolean,
    onRatingChange: (Int) -> Unit,
    onSaveClick: () -> Unit,
    onLikeClick: () -> Unit,
    onWriteReviewClick: () -> Unit,
    onBackClick: () -> Unit,
    likedReviewIds: Set<String>,
    errorMessageRes: Int?,
    onReviewLikeClick: (String) -> Unit,
    onReviewReplyClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 28.dp)
    ) {
        item {
            Column {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = stringResource(R.string.back),
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.72f),
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                ReleaseImage(cover = songReview.album.cover, albumTitle = songReview.album.title)

                Spacer(modifier = Modifier.height(24.dp))

                ReleaseTags(tags = songReview.album.tags)

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = songReview.album.title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 28.sp,
                    fontFamily = displayFontFamily,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = songReview.album.artist,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                ReleaseStats(
                    rating = songReview.rating,
                    ratingCount = songReview.ratingCount,
                    recommendRate = songReview.recommendRate
                )

                Spacer(modifier = Modifier.height(16.dp))

                UserRatingSection(
                    rating = userRating,
                    onRatingChange = onRatingChange
                )

                Spacer(modifier = Modifier.height(24.dp))

                ActionButtons(
                    isSaved = isSaved,
                    isLiked = isLiked,
                    onSaveClick = onSaveClick,
                    onLikeClick = onLikeClick
                )

                ErrorMessage(
                    messageRes = errorMessageRes,
                    modifier = Modifier.padding(top = 12.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        item {
            ReviewsHeader(onWriteReviewClick = onWriteReviewClick)
        }

        items(songReview.reviews, key = { it.id }) { review ->
            ReviewItem(
                author = review.author,
                daysAgo = review.daysAgo,
                rating = review.rating,
                content = review.content,
                likes = review.likes,
                isLiked = review.id in likedReviewIds,
                onLikeClick = { onReviewLikeClick(review.id) },
                onReplyClick = { onReviewReplyClick(review.id) }
            )
        }
    }
}

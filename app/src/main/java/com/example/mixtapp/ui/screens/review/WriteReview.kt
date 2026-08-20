package com.example.mixtapp.ui.screens.review

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.ui.components.BottomNav
import com.example.mixtapp.ui.screens.review.components.AlbumReviewCard
import com.example.mixtapp.ui.screens.review.components.MoodVibeSection
import com.example.mixtapp.ui.screens.review.components.ReviewActionsCard
import com.example.mixtapp.ui.screens.review.components.ReviewFormSection
import com.example.mixtapp.ui.screens.review.components.ReviewHeader
import com.example.mixtapp.ui.screens.review.components.ReviewRatingCard
import com.example.mixtapp.ui.screens.review.components.WriteReviewBackground
import com.example.mixtapp.ui.screens.review.model.ReviewAlbumUi
import com.example.mixtapp.ui.screens.review.model.ReviewDraftUi
import com.example.mixtapp.ui.screens.review.model.fromZeroAlbum
import com.example.mixtapp.ui.theme.DeepBackground
import com.example.mixtapp.ui.theme.MixtappTheme

private const val MaxReviewLength = 500

@Composable
fun WriteReviewScreen(
    modifier: Modifier = Modifier,
    album: ReviewAlbumUi = fromZeroAlbum,
    moods: List<String> = defaultReviewMoods,
    onCancel: () -> Unit = {},
    onPostReview: (ReviewDraftUi) -> Unit = {},
) {
    var rating by rememberSaveable { mutableStateOf(0) }
    var reviewText by rememberSaveable { mutableStateOf("") }
    var selectedMoods by rememberSaveable { mutableStateOf(emptyList<String>()) }
    var listenedDate by rememberSaveable { mutableStateOf("13/08/2026") }
    var isFavorite by rememberSaveable { mutableStateOf(false) }
    var hasPosted by rememberSaveable { mutableStateOf(false) }

    val draft = ReviewDraftUi(
        rating = rating,
        review = reviewText,
        moods = selectedMoods,
        listenedDate = listenedDate,
        isFavorite = isFavorite,
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBackground)
    ) {
        WriteReviewBackground()

        Column(modifier = Modifier.fillMaxSize()) {
            ReviewHeader(onCancel = onCancel)

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 24.dp,
                    top = 22.dp,
                    end = 24.dp,
                    bottom = 22.dp,
                )
            ) {
                item {
                    AlbumReviewCard(album = album)
                }

                item {
                    ReviewRatingCard(
                        rating = rating,
                        onRatingChange = { rating = it },
                        modifier = Modifier.padding(top = 22.dp)
                    )
                }

                item {
                    ReviewFormSection(
                        reviewText = reviewText,
                        maxLength = MaxReviewLength,
                        onReviewChange = { reviewText = it.take(MaxReviewLength) },
                        modifier = Modifier.padding(top = 22.dp)
                    )
                }

                item {
                    MoodVibeSection(
                        moods = moods,
                        selectedMoods = selectedMoods,
                        onMoodClick = { mood ->
                            selectedMoods = if (mood in selectedMoods) {
                                selectedMoods - mood
                            } else {
                                selectedMoods + mood
                            }
                        },
                        modifier = Modifier.padding(top = 22.dp)
                    )
                }

                item {
                    ReviewActionsCard(
                        listenedDate = listenedDate,
                        isFavorite = isFavorite,
                        hasPosted = hasPosted,
                        onDateChange = { listenedDate = it },
                        onFavoriteChange = { isFavorite = it },
                        onPostClick = {
                            hasPosted = true
                            onPostReview(draft)
                        },
                        modifier = Modifier.padding(top = 22.dp)
                    )
                }
            }

            BottomNav()
        }
    }
}

private val defaultReviewMoods = listOf(
    "Melancholic",
    "Nostalgic",
    "Intense",
    "Chill",
    "Romantic",
    "Energetic",
    "Ethereal",
    "Playful",
)

@Preview(showBackground = true, device = "spec:width=393dp,height=852dp")
@Composable
fun WriteReviewScreenPreview() {
    MixtappTheme(dynamicColor = false) {
        WriteReviewScreen()
    }
}

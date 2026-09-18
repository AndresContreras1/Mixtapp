package com.example.mixtapp.ui.screens.review.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mixtapp.data.model.Album
import com.example.mixtapp.ui.screens.review.MAX_REVIEW_LENGTH

@Composable
fun WriteReviewList(
    album: Album,
    rating: Int,
    reviewText: String,
    selectedMoods: List<String>,
    listenedDate: String,
    isFavorite: Boolean,
    moods: List<String>,
    errorMessageRes: Int?,
    onRatingChange: (Int) -> Unit,
    onReviewChange: (String) -> Unit,
    onMoodClick: (String) -> Unit,
    onDateChange: (String) -> Unit,
    onDatePickerClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onPostClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
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
                onRatingChange = onRatingChange,
                modifier = Modifier.padding(top = 22.dp)
            )
        }

        item {
            ReviewFormSection(
                reviewText = reviewText,
                maxLength = MAX_REVIEW_LENGTH,
                onReviewChange = onReviewChange,
                modifier = Modifier.padding(top = 22.dp)
            )
        }

        item {
            MoodVibeSection(
                moods = moods,
                selectedMoods = selectedMoods,
                onMoodClick = onMoodClick,
                modifier = Modifier.padding(top = 22.dp)
            )
        }

        item {
            ReviewActionsCard(
                listenedDate = listenedDate,
                isFavorite = isFavorite,
                errorMessageRes = errorMessageRes,
                onDateChange = onDateChange,
                onDatePickerClick = onDatePickerClick,
                onFavoriteClick = onFavoriteClick,
                onPostClick = onPostClick,
                modifier = Modifier.padding(top = 22.dp)
            )
        }
    }
}

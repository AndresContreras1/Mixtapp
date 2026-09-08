package com.example.mixtapp.ui.screens.review

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalReviewAlbumProvider
import com.example.mixtapp.ui.screens.review.components.ReviewHeader
import com.example.mixtapp.ui.screens.review.components.WriteReviewBackground
import com.example.mixtapp.ui.screens.review.components.WriteReviewList
import com.example.mixtapp.ui.screens.review.model.ReviewAlbumUi
import com.example.mixtapp.ui.screens.review.model.ReviewDraftUi
import com.example.mixtapp.ui.theme.DeepBackground
import com.example.mixtapp.ui.theme.MixtappTheme

// Recibe solo el id; el ViewModel se encarga de buscar el album
@Composable
fun WriteReviewScreen(
    albumId: String,
    writeReviewViewModel: WriteReviewViewModel,
    onCancel: () -> Unit,
    onPostReview: (ReviewDraftUi) -> Unit,
    moods: List<String> = defaultReviewMoods,
    modifier: Modifier = Modifier,
) {
    val state by writeReviewViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        writeReviewViewModel.getAlbumById(albumId = albumId)
    }

    if (state.album == null) {
        Text(text = stringResource(R.string.album_no_encontrado))
    } else {
        WriteReviewScreenContent(
            album = state.album!!,
            rating = state.rating,
            reviewText = state.reviewText,
            selectedMoods = state.selectedMoods,
            listenedDate = state.listenedDate,
            isFavorite = state.isFavorite,
            hasPosted = state.hasPosted,
            moods = moods,
            onCancel = onCancel,
            onRatingChange = { writeReviewViewModel.updateRating(rating = it) },
            onReviewChange = { writeReviewViewModel.updateReviewText(reviewText = it) },
            onMoodClick = { writeReviewViewModel.seleccionarQuitarMood(mood = it) },
            onDateChange = { writeReviewViewModel.updateListenedDate(listenedDate = it) },
            onFavoriteChange = { writeReviewViewModel.updateIsFavorite(isFavorite = it) },
            onPostClick = {
                writeReviewViewModel.publicarResena()
                onPostReview(writeReviewViewModel.crearBorrador())
            },
            modifier = modifier,
        )
    }
}

@Composable
fun WriteReviewScreenContent(
    album: ReviewAlbumUi,
    rating: Int,
    reviewText: String,
    selectedMoods: List<String>,
    listenedDate: String,
    isFavorite: Boolean,
    hasPosted: Boolean,
    moods: List<String>,
    onCancel: () -> Unit,
    onRatingChange: (Int) -> Unit,
    onReviewChange: (String) -> Unit,
    onMoodClick: (String) -> Unit,
    onDateChange: (String) -> Unit,
    onFavoriteChange: (Boolean) -> Unit,
    onPostClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBackground)
    ) {
        WriteReviewBackground()

        Column(modifier = Modifier.fillMaxSize()) {
            ReviewHeader(onCancel = onCancel)

            WriteReviewList(
                album = album,
                rating = rating,
                reviewText = reviewText,
                selectedMoods = selectedMoods,
                listenedDate = listenedDate,
                isFavorite = isFavorite,
                hasPosted = hasPosted,
                moods = moods,
                onRatingChange = onRatingChange,
                onReviewChange = onReviewChange,
                onMoodClick = onMoodClick,
                onDateChange = onDateChange,
                onFavoriteChange = onFavoriteChange,
                onPostClick = onPostClick,
                modifier = Modifier.weight(1f)
            )

        }
    }
}

@Preview(showBackground = true, device = "spec:width=393dp,height=852dp")
@Composable
fun WriteReviewScreenPreview() {
    MixtappTheme(dynamicColor = false) {
        WriteReviewScreenContent(
            album = LocalReviewAlbumProvider.albums.first(),
            rating = 0,
            reviewText = "",
            selectedMoods = emptyList(),
            listenedDate = "13/08/2026",
            isFavorite = false,
            hasPosted = false,
            moods = defaultReviewMoods,
            onCancel = {},
            onRatingChange = {},
            onReviewChange = {},
            onMoodClick = {},
            onDateChange = {},
            onFavoriteChange = {},
            onPostClick = {},
        )
    }
}

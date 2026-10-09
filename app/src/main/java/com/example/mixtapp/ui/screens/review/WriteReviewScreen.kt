package com.example.mixtapp.ui.screens.review

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalAlbumProvider
import com.example.mixtapp.data.local.LocalReviewAlbumProvider
import com.example.mixtapp.data.model.Album
import com.example.mixtapp.ui.screens.review.components.ReviewHeader
import com.example.mixtapp.ui.screens.review.components.WriteReviewBackground
import com.example.mixtapp.ui.screens.review.components.WriteReviewList
import com.example.mixtapp.ui.theme.MixtappTheme

// Recibe solo el id; el ViewModel se encarga de buscar el album
@Composable
fun WriteReviewScreen(
    albumId: String,
    writeReviewViewModel: WriteReviewViewModel,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by writeReviewViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        writeReviewViewModel.getAlbumById(albumId = albumId)
    }

    when {
        state.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        state.album == null -> {
            Text(text = stringResource(state.errorMessageRes ?: R.string.album_no_encontrado))
        }
        else -> WriteReviewScreenContent(
            album = state.album!!,
            rating = state.rating,
            reviewText = state.reviewText,
            listenedDate = state.listenedDate,
            isFavorite = state.isFavorite,
            errorMessageRes = state.errorMessageRes,
            onCancel = onCancel,
            onRatingChange = { writeReviewViewModel.updateRating(rating = it) },
            onReviewChange = { writeReviewViewModel.updateReviewText(reviewText = it) },
            onDateChange = { writeReviewViewModel.updateListenedDate(listenedDate = it) },
            onDatePickerClick = { writeReviewViewModel.usarFechaSugerida() },
            onFavoriteClick = { writeReviewViewModel.alternarFavorito() },
            onPostClick = { writeReviewViewModel.publicarResena() },
            modifier = modifier,
        )
    }
}

@Composable
fun WriteReviewScreenContent(
    album: Album,
    rating: Int,
    reviewText: String,
    listenedDate: String,
    isFavorite: Boolean,
    errorMessageRes: Int?,
    onCancel: () -> Unit,
    onRatingChange: (Int) -> Unit,
    onReviewChange: (String) -> Unit,
    onDateChange: (String) -> Unit,
    onDatePickerClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onPostClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        WriteReviewBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            ReviewHeader(onCancel = onCancel)

            WriteReviewList(
                album = album,
                rating = rating,
                reviewText = reviewText,
                listenedDate = listenedDate,
                isFavorite = isFavorite,
                errorMessageRes = errorMessageRes,
                onRatingChange = onRatingChange,
                onReviewChange = onReviewChange,
                onDateChange = onDateChange,
                onDatePickerClick = onDatePickerClick,
                onFavoriteClick = onFavoriteClick,
                onPostClick = onPostClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview(showBackground = true, device = "spec:width=393dp,height=852dp")
@Composable
fun WriteReviewScreenPreview() {
    MixtappTheme(darkTheme = true) {
        WriteReviewScreenContent(
            album = LocalAlbumProvider.albums.first(),
            rating = 0,
            reviewText = "",
            listenedDate = LocalReviewAlbumProvider.fechaEscuchaInicial,
            isFavorite = false,
            errorMessageRes = null,
            onCancel = {},
            onRatingChange = {},
            onReviewChange = {},
            onDateChange = {},
            onDatePickerClick = {},
            onFavoriteClick = {},
            onPostClick = {},
        )
    }
}

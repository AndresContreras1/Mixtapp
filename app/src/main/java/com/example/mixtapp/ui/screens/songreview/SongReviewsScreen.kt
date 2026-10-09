package com.example.mixtapp.ui.screens.songreview

import androidx.compose.foundation.layout.*
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
import com.example.mixtapp.data.local.LocalSongReviewProvider
import com.example.mixtapp.data.model.SongReviewUi
import com.example.mixtapp.ui.components.AppBackground
import com.example.mixtapp.ui.screens.songreview.components.SongReviewSections
import com.example.mixtapp.ui.theme.MixtappTheme

// Recibe solo el id; el ViewModel se encarga de buscar la cancion
@Composable
fun SongReviewsScreen(
    songId: String,
    songReviewsViewModel: SongReviewsViewModel,
    onWriteReviewClick: () -> Unit,
    onBackClick: () -> Unit,
    onReviewReplyClick: (String) -> Unit,
    onAuthorClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by songReviewsViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        songReviewsViewModel.getSongById(songId = songId)
    }

    when {
        state.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        state.song == null -> {
            Text(text = stringResource(state.errorMessageRes ?: R.string.cancion_no_encontrada))
        }
        else -> SongReviewsScreenContent(
            songReview = state.song!!,
            onRatingChange = { songReviewsViewModel.updateUserRating(rating = it) },
            onSaveClick = { songReviewsViewModel.guardarQuitarGuardado() },
            onLikeClick = { songReviewsViewModel.darQuitarLike() },
            onWriteReviewClick = onWriteReviewClick,
            onBackClick = onBackClick,
            errorMessageRes = state.errorMessageRes,
            onReviewLikeClick = { songReviewsViewModel.darQuitarLikeResena(reviewId = it) },
            onReviewReplyClick = onReviewReplyClick,
            onAuthorClick = onAuthorClick,
            modifier = modifier
        )
    }
}

@Composable
fun SongReviewsScreenContent(
    songReview: SongReviewUi,
    onRatingChange: (Int) -> Unit,
    onSaveClick: () -> Unit,
    onLikeClick: () -> Unit,
    onWriteReviewClick: () -> Unit,
    onBackClick: () -> Unit,
    errorMessageRes: Int?,
    onReviewLikeClick: (String) -> Unit,
    onReviewReplyClick: (String) -> Unit,
    onAuthorClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        AppBackground()

        SongReviewSections(
            songReview = songReview,
            onRatingChange = onRatingChange,
            onSaveClick = onSaveClick,
            onLikeClick = onLikeClick,
            onWriteReviewClick = onWriteReviewClick,
            onBackClick = onBackClick,
            errorMessageRes = errorMessageRes,
            onReviewLikeClick = onReviewLikeClick,
            onReviewReplyClick = onReviewReplyClick,
            onAuthorClick = onAuthorClick,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SongReviewsScreenPreview() {
    val song = LocalSongReviewProvider.songs.first()

    MixtappTheme(darkTheme = true) {
        SongReviewsScreenContent(
            songReview = song,
            onRatingChange = {},
            onSaveClick = {},
            onLikeClick = {},
            onWriteReviewClick = {},
            onBackClick = {},
            errorMessageRes = null,
            onReviewLikeClick = {},
            onReviewReplyClick = {},
            onAuthorClick = {}
        )
    }
}

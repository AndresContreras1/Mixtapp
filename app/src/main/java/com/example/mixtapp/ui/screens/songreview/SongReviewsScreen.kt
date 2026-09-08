package com.example.mixtapp.ui.screens.songreview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalSongReviewProvider
import com.example.mixtapp.ui.components.AppBackground
import com.example.mixtapp.ui.screens.songreview.components.SongReviewSections
import com.example.mixtapp.ui.screens.songreview.model.SongReviewUi
import com.example.mixtapp.ui.theme.DeepBackground

// Recibe solo el id; el ViewModel se encarga de buscar la cancion
@Composable
fun SongReviewsScreen(
    songId: String,
    songReviewsViewModel: SongReviewsViewModel,
    modifier: Modifier = Modifier
) {
    val state by songReviewsViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        songReviewsViewModel.getSongById(songId = songId)
    }

    if (state.song == null) {
        Text(text = stringResource(R.string.cancion_no_encontrada))
    } else {
        SongReviewsScreenContent(
            songReview = state.song!!,
            userRating = state.userRating,
            isSaved = state.isSaved,
            isLiked = state.isLiked,
            onRatingChange = { songReviewsViewModel.updateUserRating(rating = it) },
            onSaveClick = { songReviewsViewModel.guardarQuitarGuardado() },
            onLikeClick = { songReviewsViewModel.darQuitarLike() },
            modifier = modifier
        )
    }
}

@Composable
fun SongReviewsScreenContent(
    songReview: SongReviewUi,
    userRating: Int,
    isSaved: Boolean,
    isLiked: Boolean,
    onRatingChange: (Int) -> Unit,
    onSaveClick: () -> Unit,
    onLikeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBackground)
    ) {
        AppBackground()

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            SongReviewSections(
                songReview = songReview,
                userRating = userRating,
                isSaved = isSaved,
                isLiked = isLiked,
                onRatingChange = onRatingChange,
                onSaveClick = onSaveClick,
                onLikeClick = onLikeClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

        }
    }
}

@Preview(showBackground = true)
@Composable
fun SongReviewsScreenPreview() {
    val song = LocalSongReviewProvider.songs.first()

    SongReviewsScreenContent(
        songReview = song,
        userRating = song.userRating,
        isSaved = song.isSaved,
        isLiked = song.isLiked,
        onRatingChange = {},
        onSaveClick = {},
        onLikeClick = {}
    )
}

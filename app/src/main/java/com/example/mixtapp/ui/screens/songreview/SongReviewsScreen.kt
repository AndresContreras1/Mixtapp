package com.example.mixtapp.ui.screens.songreview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalSongReviewProvider
import com.example.mixtapp.ui.components.AppBackground
import com.example.mixtapp.ui.screens.songreview.components.*
import com.example.mixtapp.ui.screens.songreview.model.SongReviewUi
import com.example.mixtapp.ui.theme.DeepBackground
import com.example.mixtapp.ui.theme.TextPink

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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 28.dp)
            ) {
                ReleaseImage(coverRes = songReview.coverRes)

                Spacer(modifier = Modifier.height(24.dp))

                ReleaseTags(tags = songReview.tags)

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = songReview.title,
                    color = Color.White,
                    fontSize = 28.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = songReview.artist,
                    color = TextPink.copy(alpha = 0.7f),
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

                Spacer(modifier = Modifier.height(32.dp))

                ReviewsSection(reviews = songReview.reviews)
            }

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

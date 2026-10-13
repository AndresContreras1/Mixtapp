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
import com.example.mixtapp.ui.screens.songreview.components.SongReviewSections
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppBackground

// Recibe solo el id; el ViewModel se encarga de buscar el album
@Composable
fun SongReviewsScreen(
    songId: String,
    songReviewsViewModel: SongReviewsViewModel,
    onWriteReviewClick: () -> Unit,
    onBackClick: () -> Unit,
    onAuthorClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by songReviewsViewModel.uiState.collectAsState()

    // Necesita el id que llega por la ruta, asi que se pide en un LaunchedEffect
    LaunchedEffect(Unit) {
        songReviewsViewModel.getSongById(songId = songId)
    }

    when {
        state.isLoading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        state.song == null -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = stringResource(state.errorMessageRes ?: R.string.album_no_encontrado))
            }
        }
        else -> SongReviewsScreenContent(
            songReview = state.song!!,
            miResenaId = state.miResenaId,
            errorMessageRes = state.errorMessageRes,
            onWriteReviewClick = onWriteReviewClick,
            onDeleteMyReviewClick = { songReviewsViewModel.eliminarMiResena() },
            onBackClick = onBackClick,
            onAuthorClick = onAuthorClick,
            modifier = modifier
        )
    }
}

@Composable
fun SongReviewsScreenContent(
    songReview: SongReviewUi,
    miResenaId: String?,
    errorMessageRes: Int?,
    onWriteReviewClick: () -> Unit,
    onDeleteMyReviewClick: () -> Unit,
    onBackClick: () -> Unit,
    onAuthorClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        AppBackground()

        SongReviewSections(
            songReview = songReview,
            miResenaId = miResenaId,
            errorMessageRes = errorMessageRes,
            onWriteReviewClick = onWriteReviewClick,
            onDeleteMyReviewClick = onDeleteMyReviewClick,
            onBackClick = onBackClick,
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
            miResenaId = null,
            errorMessageRes = null,
            onWriteReviewClick = {},
            onDeleteMyReviewClick = {},
            onBackClick = {},
            onAuthorClick = {}
        )
    }
}

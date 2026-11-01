package com.example.mixtapp.ui.screens.albumdetail

import androidx.compose.foundation.layout.*
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
import com.example.mixtapp.data.local.LocalAlbumReviewsProvider
import com.example.mixtapp.data.model.AlbumReviewsUi
import com.example.mixtapp.ui.screens.albumdetail.components.AlbumDetailSections
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppBackground
import com.example.mixtapp.ui.utils.LoadingBox

// Recibe solo el id; el ViewModel se encarga de buscar el album
@Composable
fun AlbumDetailScreen(
    albumId: String,
    albumDetailViewModel: AlbumDetailViewModel,
    onWriteReviewClick: () -> Unit,
    onBackClick: () -> Unit,
    onAuthorClick: (String) -> Unit,
    onCommentsClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by albumDetailViewModel.uiState.collectAsState()

    // Necesita el id que llega por la ruta, asi que empieza a escuchar en un LaunchedEffect. Al
    // volver de escribir mi resena no hace falta pedir nada: el flujo ya la trae
    LaunchedEffect(Unit) {
        albumDetailViewModel.escucharAlbum(albumId = albumId)
    }

    // Con una copia local, Kotlin sabe que no es nula despues de revisarla: no hace falta el !!
    val albumReviews = state.albumReviews

    when {
        state.isLoading -> {
            LoadingBox(modifier = modifier)
        }
        albumReviews == null -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = stringResource(state.errorMessageRes ?: R.string.album_no_encontrado))
            }
        }
        else -> AlbumDetailScreenContent(
            albumReviews = albumReviews,
            miResenaId = state.miResenaId,
            onLikeClick = { albumDetailViewModel.sendOrDeleteLike(reviewId = it) },
            errorMessageRes = state.errorMessageRes,
            onWriteReviewClick = onWriteReviewClick,
            onDeleteMyReviewClick = { albumDetailViewModel.eliminarMiResena() },
            onBackClick = onBackClick,
            onAuthorClick = onAuthorClick,
            onCommentsClick = onCommentsClick,
            modifier = modifier
        )
    }
}

@Composable
fun AlbumDetailScreenContent(
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
    Box(modifier = modifier.fillMaxSize()) {
        AppBackground()

        AlbumDetailSections(
            albumReviews = albumReviews,
            miResenaId = miResenaId,
            onLikeClick = onLikeClick,
            errorMessageRes = errorMessageRes,
            onWriteReviewClick = onWriteReviewClick,
            onDeleteMyReviewClick = onDeleteMyReviewClick,
            onBackClick = onBackClick,
            onAuthorClick = onAuthorClick,
            onCommentsClick = onCommentsClick,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AlbumDetailScreenPreview() {
    val albumReviews = LocalAlbumReviewsProvider.albumReviews.first()

    MixtappTheme(darkTheme = true) {
        AlbumDetailScreenContent(
            albumReviews = albumReviews,
            miResenaId = null,
            onLikeClick = {},
            errorMessageRes = null,
            onWriteReviewClick = {},
            onDeleteMyReviewClick = {},
            onBackClick = {},
            onAuthorClick = {},
            onCommentsClick = {}
        )
    }
}

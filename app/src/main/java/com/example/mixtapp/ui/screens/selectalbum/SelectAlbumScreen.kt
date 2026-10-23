package com.example.mixtapp.ui.screens.selectalbum

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalAlbumReviewsProvider
import com.example.mixtapp.data.model.AlbumReviewsUi
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.theme.displayFontFamily
import com.example.mixtapp.ui.utils.BackButton
import com.example.mixtapp.ui.utils.LoadingBox
import com.example.mixtapp.ui.utils.ReviewAlbumRow

// El boton + abre esta pantalla para elegir el album antes de escribir la resena
@Composable
fun SelectAlbumScreen(
    selectAlbumViewModel: SelectAlbumViewModel,
    onAlbumClick: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by selectAlbumViewModel.uiState.collectAsState()

    when {
        state.isLoading -> {
            LoadingBox(modifier = modifier)
        }
        // Sin albumes cargados solo se muestra el motivo del error
        state.errorMessageRes != null && state.albums.isEmpty() -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = stringResource(state.errorMessageRes ?: R.string.error_cargar_contenido))
            }
        }
        else -> SelectAlbumScreenContent(
            albums = state.albums,
            onAlbumClick = onAlbumClick,
            onBackClick = onBackClick,
            modifier = modifier
        )
    }
}

@Composable
fun SelectAlbumScreenContent(
    albums: List<AlbumReviewsUi>,
    onAlbumClick: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 28.dp)
        ) {
            item {
                BackButton(onBackClick = onBackClick)

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.elegir_album_titulo),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = displayFontFamily
                )

                Text(
                    text = stringResource(R.string.elegir_album_descripcion),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            items(albums.size) { index ->
                ReviewAlbumRow(
                    cover = albums[index].album.cover,
                    albumTitle = albums[index].album.title,
                    artistName = albums[index].album.artist,
                    rating = albums[index].estrellas,
                    coverSize = 64.dp,
                    coverCorner = 10.dp,
                    starSize = 14.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAlbumClick(albums[index].album.id) }
                        .padding(vertical = 10.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=393dp,height=852dp")
@Composable
fun SelectAlbumScreenPreview() {
    MixtappTheme(darkTheme = true) {
        SelectAlbumScreenContent(
            albums = LocalAlbumReviewsProvider.albumReviews,
            onAlbumClick = {},
            onBackClick = {}
        )
    }
}

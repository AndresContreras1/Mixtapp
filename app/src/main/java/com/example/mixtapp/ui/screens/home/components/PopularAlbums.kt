package com.example.mixtapp.ui.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mixtapp.data.model.SongReviewUi

// Todos los albumes del backend en una fila que se desliza
@Composable
fun PopularAlbums(
    albums: List<SongReviewUi>,
    onAlbumClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(albums.size) { index ->
            AlbumItem(
                title = albums[index].album.title,
                artist = albums[index].album.artist,
                cover = albums[index].album.cover,
                modifier = Modifier.width(100.dp),
                onClick = { onAlbumClick(albums[index].album.id) }
            )
        }
    }
}

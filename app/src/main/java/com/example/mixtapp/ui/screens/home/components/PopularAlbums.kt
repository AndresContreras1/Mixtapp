package com.example.mixtapp.ui.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mixtapp.data.model.SongReviewUi

@Composable
fun PopularAlbums(
    albums: List<SongReviewUi>,
    onAlbumClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        albums.forEach { songReview ->
            AlbumItem(
                title = songReview.album.title,
                artist = songReview.album.artist,
                cover = songReview.album.cover,
                modifier = Modifier.weight(1f),
                onClick = { onAlbumClick(songReview.album.id) }
            )
        }
    }
}
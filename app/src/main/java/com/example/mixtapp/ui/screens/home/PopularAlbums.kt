package com.example.mixtapp.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R

@Composable
fun PopularAlbums(
    onSelectTrack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AlbumItem(
            title = "Teatro D'ira Vol I",
            artist = "Måneskin",
            imageRes = R.drawable.teatro_dira_vol_i_portada,
            modifier = Modifier.weight(1f),
            onClick = onSelectTrack
        )
        AlbumItem(
            title = "Rush!",
            artist = "Måneskin",
            imageRes = R.drawable.rush_portada,
            modifier = Modifier.weight(1f),
            onClick = onSelectTrack
        )
        AlbumItem(
            title = "Finisterra",
            artist = "Mägo de Oz",
            imageRes = R.drawable.finisterra_portada,
            modifier = Modifier.weight(1f),
            onClick = onSelectTrack
        )
    }
}
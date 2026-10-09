package com.example.mixtapp.data.local

import com.example.mixtapp.data.model.ProfileUi

object LocalProfileProvider {

    val profile = ProfileUi(
        id = "yourname",
        username = "Yourname",
        joinDate = "marzo de 2025",
        albumsCount = 64,
        listsCount = 18,
        favoriteAlbums = LocalAlbumProvider.albums.take(4),
        ratingBars = listOf(
            0.1f, 0.2f, 0.15f, 0.3f, 0.4f, 0.35f, 0.5f, 0.8f, 1f, 0.7f, 0.6f
        ),
    )
}

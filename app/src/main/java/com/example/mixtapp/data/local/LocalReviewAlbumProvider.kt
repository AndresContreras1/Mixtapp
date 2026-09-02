package com.example.mixtapp.data.local

import com.example.mixtapp.R
import com.example.mixtapp.ui.screens.review.model.ReviewAlbumUi

object LocalReviewAlbumProvider {

    // El id es el mismo de LocalSongReviewProvider, para poder abrir la
    // pantalla de resena desde el detalle de una cancion
    val albums = listOf(
        ReviewAlbumUi(
            id = "4",
            title = "From Zero",
            artist = "Linkin Park",
            year = "2024",
            genre = "Rock",
            coverRes = R.drawable.from_zero,
        ),
    )
}

package com.example.mixtapp.data.local

import com.example.mixtapp.R
import com.example.mixtapp.ui.screens.review.model.ReviewAlbumUi

object LocalReviewAlbumProvider {

    val fechaEscuchaInicial = "13/08/2026"

    val fechaEscuchaSugerida = "20/08/2026"

    // Moods disponibles al escribir una resena
    val moods = listOf(
        "Melancholic",
        "Nostalgic",
        "Intense",
        "Chill",
        "Romantic",
        "Energetic",
        "Ethereal",
        "Playful",
    )

    // El id es el mismo de LocalSongReviewProvider, para poder abrir la
    // pantalla de resena desde el detalle de una cancion
    val albums = listOf(
        ReviewAlbumUi(
            id = "1",
            title = "Teatro D'ira Vol I",
            artist = "Maneskin",
            year = "2021",
            genre = "Hard Rock",
            coverRes = R.drawable.teatro_dira_vol_i_portada,
        ),
        ReviewAlbumUi(
            id = "2",
            title = "Rush!",
            artist = "Maneskin",
            year = "2023",
            genre = "Dance Punk",
            coverRes = R.drawable.rush_portada,
        ),
        ReviewAlbumUi(
            id = "3",
            title = "Finisterra",
            artist = "Mago de Oz",
            year = "2000",
            genre = "Power Metal",
            coverRes = R.drawable.finisterra_portada,
        ),
        ReviewAlbumUi(
            id = "4",
            title = "From Zero",
            artist = "Linkin Park",
            year = "2024",
            genre = "Rock",
            coverRes = R.drawable.from_zero,
        ),
        ReviewAlbumUi(
            id = "5",
            title = "The Sharpest Lives",
            artist = "My Chemical Romance",
            year = "2006",
            genre = "Rock Alternativo",
            coverRes = R.drawable.rush_portada,
        ),
    )
}

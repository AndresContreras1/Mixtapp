package com.example.mixtapp.data.local

import com.example.mixtapp.ui.screens.review.model.ReviewAlbumUi

object LocalReviewAlbumProvider {

    val fechaEscuchaInicial = "13/08/2026"

    val fechaEscuchaSugerida = "20/08/2026"

    // Moods disponibles al escribir una resena
    val moods = listOf(
        "Melancólico",
        "Nostálgico",
        "Intenso",
        "Relajado",
        "Romántico",
        "Enérgico",
        "Etéreo",
        "Divertido",
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
            cover = AlbumCovers.TEATRO_DIRA,
        ),
        ReviewAlbumUi(
            id = "2",
            title = "Rush!",
            artist = "Maneskin",
            year = "2023",
            genre = "Dance Punk",
            cover = AlbumCovers.RUSH,
        ),
        ReviewAlbumUi(
            id = "3",
            title = "Finisterra",
            artist = "Mago de Oz",
            year = "2000",
            genre = "Power Metal",
            cover = AlbumCovers.FINISTERRA,
        ),
        ReviewAlbumUi(
            id = "4",
            title = "From Zero",
            artist = "Linkin Park",
            year = "2024",
            genre = "Rock",
            cover = AlbumCovers.FROM_ZERO,
        ),
        ReviewAlbumUi(
            id = "5",
            title = "The Sharpest Lives",
            artist = "My Chemical Romance",
            year = "2006",
            genre = "Rock Alternativo",
            cover = AlbumCovers.RUSH,
        ),
    )
}

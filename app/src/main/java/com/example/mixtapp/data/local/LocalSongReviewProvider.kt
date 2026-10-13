package com.example.mixtapp.data.local

import com.example.mixtapp.data.model.SongReviewItemUi
import com.example.mixtapp.data.model.SongReviewUi

// Solo para las previews y las pruebas: la app lee los albumes y sus resenas de la base remota
object LocalSongReviewProvider {

    val songs = listOf(
        SongReviewUi(
            id = "1",
            album = LocalAlbumProvider.teatroDira,
            rating = 5.0,
            estrellas = 5,
            ratingCount = "1",
            recommendRate = 100,
            userRating = 0,
            reviews = listOf(
                SongReviewItemUi(
                    id = "review-1",
                    author = "Mateo Gómez",
                    authorId = "2",
                    authorImage = "",
                    fecha = "2026-08-15",
                    rating = 5,
                    content = "Teatro d'ira: Vol. I captures Maneskin at their absolute peak. Razor-sharp riffs, explosive live energy and Damiano's theatrical vocals turn pure attitude into a tight, unforgettable record.",
                    esMia = false,
                )
            ),
        ),
        SongReviewUi(
            id = "2",
            album = LocalAlbumProvider.rush,
            rating = 4.0,
            estrellas = 4,
            ratingCount = "1",
            recommendRate = 100,
            userRating = 0,
            reviews = listOf(
                SongReviewItemUi(
                    id = "review-2",
                    author = "Valentina Torres",
                    authorId = "3",
                    authorImage = "",
                    fecha = "2026-08-12",
                    rating = 4,
                    content = "An infectious, high-octane pop-rock spectacle built for global arenas. It trades some of the raw Italian grit for polished dance-punk grooves, but the attitude never drops.",
                    esMia = false,
                )
            ),
        ),
        SongReviewUi(
            id = "3",
            album = LocalAlbumProvider.finisterra,
            rating = 5.0,
            estrellas = 5,
            ratingCount = "1",
            recommendRate = 100,
            userRating = 0,
            reviews = listOf(
                SongReviewItemUi(
                    id = "review-3",
                    author = "Santiago López",
                    authorId = "4",
                    authorImage = "",
                    fecha = "2026-08-09",
                    rating = 5,
                    content = "Mago de Oz's ambitious folk metal masterpiece. Power metal riffs blended with Celtic flutes and violins turn an epic medieval concept into a legendary album.",
                    esMia = false,
                )
            ),
        ),
        SongReviewUi(
            id = "4",
            album = LocalAlbumProvider.fromZero,
            rating = 4.0,
            estrellas = 4,
            ratingCount = "1",
            recommendRate = 100,
            userRating = 0,
            reviews = listOf(
                SongReviewItemUi(
                    id = "review-4",
                    author = "Camila Herrera",
                    authorId = "5",
                    authorImage = "",
                    fecha = "2026-08-14",
                    rating = 4,
                    content = "From Zero strikes a balance between heavy nostalgia and genuine reinvention. Emily Armstrong commands every track with raw power, signaling a confident new chapter.",
                    esMia = false,
                )
            ),
        ),
        SongReviewUi(
            id = "5",
            album = LocalAlbumProvider.theSharpestLives,
            rating = 5.0,
            estrellas = 5,
            ratingCount = "1",
            recommendRate = 100,
            userRating = 0,
            reviews = listOf(
                SongReviewItemUi(
                    id = "review-5",
                    author = "Mateo Gómez",
                    authorId = "2",
                    authorImage = "",
                    fecha = "2026-08-14",
                    rating = 5,
                    content = "\"The Sharpest Lives\" is an absolute rush on The Black Parade, blending dark, frantic energy with an insanely catchy hook. Gerard Way's theatrical vocals and the sharp guitar work turn chaotic self-destruction into one of My Chemical Romance's most addictive anthems.",
                    esMia = false,
                )
            ),
        ),
        SongReviewUi(
            id = "6",
            album = LocalAlbumProvider.toxicity,
            rating = 0.0,
            estrellas = 0,
            ratingCount = "0",
            recommendRate = 0,
            userRating = 0,
            reviews = emptyList(),
        ),
        SongReviewUi(
            id = "7",
            album = LocalAlbumProvider.randomAccessMemories,
            rating = 0.0,
            estrellas = 0,
            ratingCount = "0",
            recommendRate = 0,
            userRating = 0,
            reviews = emptyList(),
        ),
        SongReviewUi(
            id = "8",
            album = LocalAlbumProvider.theBlackParade,
            rating = 0.0,
            estrellas = 0,
            ratingCount = "0",
            recommendRate = 0,
            userRating = 0,
            reviews = emptyList(),
        ),
    )

    // Los tres albumes que se muestran en la preview de la pantalla principal
    val popularSongs: List<SongReviewUi>
        get() = songs.take(3)

    // El album destacado de la preview de la pantalla principal
    val trendingSong: SongReviewUi
        get() = songs[2]
}

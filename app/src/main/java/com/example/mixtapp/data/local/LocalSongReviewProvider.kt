package com.example.mixtapp.data.local

import com.example.mixtapp.R
import com.example.mixtapp.ui.screens.songreview.model.SongReviewItemUi
import com.example.mixtapp.ui.screens.songreview.model.SongReviewUi

object LocalSongReviewProvider {

    val songs = listOf(
        SongReviewUi(
            id = "1",
            coverRes = R.drawable.teatro_dira_vol_i_portada,
            tags = listOf("2021", "rock", "Hard Rock"),
            title = "Teatro D'ira Vol I",
            artist = "Maneskin",
            rating = "4.8",
            ratingCount = "41.2k",
            recommendRate = "97%",
            userRating = 0,
            isSaved = false,
            isLiked = false,
            reviews = listOf(
                SongReviewItemUi(
                    author = "Priya",
                    daysAgo = "2d ago",
                    rating = 5,
                    content = "Teatro d'ira: Vol. I captures Maneskin at their absolute peak. Razor-sharp riffs, explosive live energy and Damiano's theatrical vocals turn pure attitude into a tight, unforgettable record.",
                    likes = 31,
                )
            ),
        ),
        SongReviewUi(
            id = "2",
            coverRes = R.drawable.rush_portada,
            tags = listOf("2023", "pop rock", "Dance Punk"),
            title = "Rush!",
            artist = "Maneskin",
            rating = "4.2",
            ratingCount = "33.7k",
            recommendRate = "89%",
            userRating = 0,
            isSaved = false,
            isLiked = false,
            reviews = listOf(
                SongReviewItemUi(
                    author = "Leo",
                    daysAgo = "5d ago",
                    rating = 4,
                    content = "An infectious, high-octane pop-rock spectacle built for global arenas. It trades some of the raw Italian grit for polished dance-punk grooves, but the attitude never drops.",
                    likes = 18,
                )
            ),
        ),
        SongReviewUi(
            id = "3",
            coverRes = R.drawable.finisterra_portada,
            tags = listOf("2000", "folk metal", "Power Metal"),
            title = "Finisterra",
            artist = "Mago de Oz",
            rating = "4.7",
            ratingCount = "22.9k",
            recommendRate = "95%",
            userRating = 0,
            isSaved = false,
            isLiked = false,
            reviews = listOf(
                SongReviewItemUi(
                    author = "Alex",
                    daysAgo = "1w ago",
                    rating = 5,
                    content = "Mago de Oz's ambitious folk metal masterpiece. Power metal riffs blended with Celtic flutes and violins turn an epic medieval concept into a legendary album.",
                    likes = 27,
                )
            ),
        ),
        SongReviewUi(
            id = "4",
            coverRes = R.drawable.from_zero,
            tags = listOf("2024", "nu metal", "Rock Alternativo"),
            title = "From Zero",
            artist = "Linkin Park",
            rating = "4.4",
            ratingCount = "58.1k",
            recommendRate = "92%",
            userRating = 0,
            isSaved = false,
            isLiked = false,
            reviews = listOf(
                SongReviewItemUi(
                    author = "Soph",
                    daysAgo = "4d ago",
                    rating = 4,
                    content = "From Zero strikes a balance between heavy nostalgia and genuine reinvention. Emily Armstrong commands every track with raw power, signaling a confident new chapter.",
                    likes = 42,
                )
            ),
        ),
        SongReviewUi(
            id = "5",
            coverRes = R.drawable.rush_portada,
            tags = listOf("2006", "emo", "Rock Alternativo"),
            title = "The Sharpest Lives",
            artist = "My Chemical Romance",
            rating = "4.5",
            ratingCount = "28.4k",
            recommendRate = "94%",
            userRating = 0,
            isSaved = false,
            isLiked = false,
            reviews = listOf(
                SongReviewItemUi(
                    author = "Priya",
                    daysAgo = "3d ago",
                    rating = 5,
                    content = "\"The Sharpest Lives\" is an absolute rush on The Black Parade, blending dark, frantic energy with an insanely catchy hook. Gerard Way's theatrical vocals and the sharp guitar work turn chaotic self-destruction into one of My Chemical Romance's most addictive anthems.",
                    likes = 24,
                )
            ),
        ),
    )

    // Los tres albumes que se muestran en la pantalla principal
    val popularSongs = songs.take(3)

    // El album destacado de la pantalla principal
    val trendingSong = songs[2]
}
